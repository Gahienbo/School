
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <semaphore.h>
#include <fcntl.h>
#include <sys/shm.h>
#include <signal.h>
#include <sys/mman.h>

#define SharedCreate(pointer) {(pointer) = mmap(NULL,sizeof(*(pointer)), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS,-1,0);}
#define SharedDelete(pointer) {munmap((pointer), sizeof((pointer)));}
typedef struct{
    int P;      //P = pocet osob vygenerovanych v kazde kategorii
    int H;      //H = max doba po ktere je vygenerovan proces HACKER
    int S;      //S = max doba po ktere je vygenerovan proces SERF
    int R;      //R = max doba plavby v ms
    int W;      //W = max doba v ms, po ktere se osoba vraci na molo
    int C;      //C = kapacita mola
} param;
sem_t *EndSem = NULL; //semafor pro ukonceni fce main
sem_t *LineSem = NULL;
sem_t *WriteSem = NULL;
sem_t *BoardSem = NULL;
sem_t *CrewSem = NULL;
sem_t *GetOffSem = NULL;

int *Poradi = 0;
int *MoloHackers = 0;
int *MoloSerfs = 0;
int *LandCount = 0;
int *VoyageCount = 0;

FILE *ProjLog = NULL;

const char *CATEGORY[] =
{
    [1] = "HACK",
    [2] = "SERF",
};

#define Captain 2
#define Member 1

int Init(); //inicializuje semafory,sdilene promenne
void CleanUp(); // uklizi sdilene pameti,semafory
float MySleep(int Max); //generovani nahodneho cisla z intervalu daneho horni hranici Max
int RoleCheck(); //prideluje role 1=Member 2=Captain
void PersonProcess(int Id,int Category,param *pm); //hlavni fce procesu SERF a HACKER Id = id procesu predano z fce PersonGen, Category = kategorie SERF/HACKER, pm = struktura s parametry programu
void PersonGen(int PCount,int Delay,int Category,param *pm); //pomocna fce pro generovani procesů SERF/HACKER PCount = pocet osob zadane argumentem, Delay = max prodleva mezi generovanim procesu, pm = struktura s parametry programu
int LoadArg(int argc, char *argv[], param *pm); //nacita arguemnty



int main(int argc, char *argv[]) {
  if ((ProjLog = fopen("proj2.out", "w+")) == NULL) {
      fprintf(stderr, "Err while creating/opening LOG");
       return -1;
     }
  if (Init()==-1) {
    printf("Shared memory and semaphore inicialization error\n");
    CleanUp();
    return -1;
  }
  param *pm, ini;
  pm = &ini;
  if (LoadArg(argc, argv, pm) == -1 ) {
    fprintf(stderr,"invalid arguments run the program without arguments to see the help message\n");
    CleanUp();
    exit(1);
  }
  else {
    *VoyageCount = pm -> P*2/4; //nastavuje promennou *VoyageCount na pocet plaveb ktere se s danymi arguemnty maji provest
    pid_t Hackers = fork(); //vytvoreni pomocneho procesu HACKER
    if (Hackers == 0) {
      PersonGen(pm -> P,pm -> H,1,pm);
    }
    pid_t Serfs = fork(); //vytvoreni pomocneho procesu SERF
    if (Serfs == 0 ) {
      PersonGen(pm -> P,pm -> S,2,pm);
    }
  }
  sem_wait(EndSem); //hlavni proces ceka na ukonceni vsech plaveb
  CleanUp();
  return 0;
}

int Init() {
  SharedCreate(Poradi);
  SharedCreate(MoloHackers);
  SharedCreate(MoloSerfs);
  SharedCreate(LandCount);
  SharedCreate(VoyageCount);
  *LandCount += 3;
  if ((EndSem = sem_open("/xhrani02.ios", O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) return -1;
  else if ((LineSem = sem_open("/xhrani02.ios2", O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) return -1;
  else if ((WriteSem = sem_open("/xhrani02.ios4", O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) return -1;
  else if ((CrewSem = sem_open("/xhrani02.ios5", O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) return -1;
  else if ((BoardSem = sem_open("/xhrani02.ios6", O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) return -1;
  else if ((GetOffSem = sem_open("/xhrani02.ios7", O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) return -1;
  return 0;
}
void CleanUp() {
  SharedDelete(Poradi);
  SharedDelete(MoloHackers);
  SharedDelete(MoloSerfs);
  SharedDelete(LandCount);
  SharedDelete(VoyageCount);
  sem_close(EndSem);
  sem_close(LineSem);
  sem_close(WriteSem);
  sem_close(CrewSem);
  sem_close(BoardSem);
  sem_close(GetOffSem);
  sem_unlink("/xhrani02.ios");
  sem_unlink("/xhrani02.ios2");
  sem_unlink("/xhrani02.ios4");
  sem_unlink("/xhrani02.ios5");
  sem_unlink("/xhrani02.ios6");
  sem_unlink("/xhrani02.ios7");
}

float MySleep(int Max) {
  float Duration = (rand() % (Max + 1));
  return Duration;
}

int RoleCheck() {
  if (*LandCount < 3) {
    *LandCount += 1;
    return Member;
  }
  else if (*MoloHackers >= 4) {
    *MoloHackers -= 4;
    return Captain;
  }
  else if (*MoloSerfs >=4) {
    *MoloSerfs -= 4;
    return Captain;
  }
  else if (*MoloHackers >= 2 && *MoloSerfs >= 2) {
    *MoloSerfs -= 2;
    *MoloHackers -= 2;
    return Captain;
  }
  return Member;
}

void PersonProcess(int Id,int Category,param *pm) {
  sem_wait(WriteSem);
  *Poradi += 1;
  fprintf(ProjLog,"%d: %s %d: starts\n",*Poradi,CATEGORY[Category],Id);
  fflush(ProjLog);
  sem_post(WriteSem);

  sem_wait(LineSem);

  while ( (*MoloHackers+*MoloSerfs) >= pm->C ) {

    sem_wait(WriteSem);
    *Poradi += 1;
    fprintf(ProjLog,"%d : %s %d : leaves queue      : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
    sem_post(WriteSem);

    usleep(MySleep(pm->W));

    sem_wait(WriteSem);
    *Poradi += 1;
    fprintf(ProjLog,"%d : %s %d : is back\n",*Poradi,CATEGORY[Category],Id);
    fflush(ProjLog);
    sem_post(WriteSem);
  }

  sem_wait(WriteSem);
  *Poradi += 1;
  if (Category == 1) {
    *MoloHackers += 1;
    fprintf(ProjLog,"%d : %s  %d : waits      : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
  }
  else {
    *MoloSerfs += 1;
    fprintf(ProjLog,"%d : %s %d : waits      : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
  }

  sem_post(LineSem);
  sem_post(WriteSem);

  sem_wait(BoardSem);

  sem_wait(WriteSem);
  int Role = RoleCheck();
  sem_post(WriteSem);

  if (Role == Member) {
    sem_post(BoardSem);
    sem_wait(CrewSem);
    sem_wait(WriteSem);
    *Poradi += 1;
    fprintf(ProjLog,"%d : %s %d : member exits      : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
    *LandCount -= 1;
    sem_post(WriteSem);
    if (*LandCount == 0) {
      sem_post(GetOffSem);
    }

  }
  if (Role == Captain) {
    sem_wait(WriteSem);
    *Poradi += 1;
    fprintf(ProjLog,"%d : %s %d : boards     : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
    sem_post(WriteSem);

    usleep(MySleep(pm->R));

    for (int i = 0; i < 3; i++) {
      sem_post(CrewSem);
    }
    sem_wait(GetOffSem);

    sem_wait(WriteSem);
    *Poradi += 1;
    fprintf(ProjLog,  "%d : %s %d : captain exits      : %d : %d\n", *Poradi,CATEGORY[Category], Id, *MoloHackers, *MoloSerfs);
    fflush(ProjLog);
    *VoyageCount -= 1;
    sem_post(WriteSem);
    sem_post(BoardSem);

    if (*VoyageCount == 0) {
      sem_post(EndSem);
    }
  }
  exit(0);
}
void PersonGen(int PCount,int Delay,int Category,param *pm) {
  for (int i = 1; i <= PCount; i++) {
    usleep(MySleep(Delay));
    pid_t RiderID = fork();
    if (RiderID == 0) {
      PersonProcess(i,Category,pm);
    }
    if (RiderID < 0) {
      exit(1);
    }
  }
  exit(0);
}
int LoadArg(int argc, char *argv[], param *pm) {
  char *Err;
  if (argc == 1) {
    printf("HELP\n");
    return -1;
  }
  else if (argc == 7) {
     pm -> P = strtoul(argv[1], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> P < 2 || (pm -> P % 2) !=0) {
       fprintf(stderr,"Argument P has to be >2 and has to be even\n");
       return -1;
     }
     pm -> H = strtoul(argv[2], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> H < 0 || pm -> H > 2000) {
       fprintf(stderr,"Argument H has to be from interval <0,200> \n");
       return -1;
     }
     pm -> S = strtoul(argv[3], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> S < 0 || pm -> S > 2000) {
       fprintf(stderr,"Argument H has to be from interval <0,200> \n");
       return -1;
     }
     pm -> R = strtoul(argv[4], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> R < 0 || pm -> R > 2000) {
       fprintf(stderr,"Argument H has to be from interval <0,200> \n");
       return -1;
     }
     pm -> W = strtoul(argv[5], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> W < 20 || pm -> W > 2000) {
       fprintf(stderr,"Argument H has to be from interval <20,200> \n");
       return -1;
     }
     pm -> C = strtoul(argv[6], &Err, 10);
     if (*Err != '\0') {
               fprintf(stderr,"Argument has to be integer\n");
               return -1;
           }
     else if (pm -> C < 5) {
       fprintf(stderr,"Argument C has to be greater than 5\n");
       return -1;
     }
  }
  return 1;
}
