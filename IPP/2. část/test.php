<?php
//postara se o argumenty
$argumentHandler = new ArgumentHandler();
$argumentHandler->checkArgs($argc,$argv);

//objekt generujici html stranku
$html = new htmlGenerator;
$html->headers();

//podle argumentu se skript spusti v modu both/parse-only/int-only
if ($argumentHandler->parseOnlyFlag == 1) { //parse only
  $directoryHandler = new DirectoryHandler($argumentHandler->directoryPath,$argumentHandler->recursiveFlag,2,$html,$argumentHandler->jexamxmlPath,$argumentHandler->intScriptPath,$argumentHandler->parseScriptPath);
}
elseif ($argumentHandler->intOnlyFlag == 1) { // int only
  $directoryHandler = new DirectoryHandler($argumentHandler->directoryPath,$argumentHandler->recursiveFlag,1,$html,$argumentHandler->jexamxmlPath,$argumentHandler->intScriptPath,$argumentHandler->parseScriptPath);
}
else { //both  
  $directoryHandler = new DirectoryHandler($argumentHandler->directoryPath,$argumentHandler->recursiveFlag,0,$html,$argumentHandler->jexamxmlPath,$argumentHandler->intScriptPath,$argumentHandler->parseScriptPath);
}

//spusteni pruchodu testu
$directoryHandler->goTroughDirectory($argumentHandler->directoryPath);
//ukonceni tabulky
$html->endTable();
//vygenerovani souhrnu testu
$html->generateSummary($directoryHandler->failedCount,$directoryHandler->passedCount);

//trida starajici se o argumenty
class ArgumentHandler {
  public $recursiveFlag = 0;
  public $parseOnlyFlag = 0;
  public $intOnlyFlag = 0;
  public $parseScriptFlag = 0;
  public $intScriptFlag = 0;

  public $directoryPath = "/";
  public $parseScriptPath = "parse.php";
  public $intScriptPath = "interpret.py";
  public $jexamxmlPath = "/pub/courses/ipp/jexamxml/jexamxml.jar";

  public function checkArgs($argc,$argv) {

    for ($i =1 ;$i<$argc;$i++) {
      if ($argv[$i] == "--help" && $argc == 2){
        echo("Skript test.php slouzi pro testovani skriptu interpet.py parse.php\n");
        echo("pouziti ./test.php --directory=path\n");
        echo("volitelne paramatery: \n --recursive, rekurzivni prohledavani dane slozky\n");
        echo("--parse-script=path, zmena cesty ke skriptu parse.php, defaultne hleda skript v aktualni slozce\n");
        echo("--int-script=path, zmena cesty ke skriptu interpret.py, defaultne hleda skript v aktualni slozce\n");
        echo("--int-only testuje pouze skript interpret.py, nekompatibilni s parametrem --parse-script=path a --parse-only \n");
        echo("--parse-only testuje pouze skript parse.php, nekompatibilni s parametrem --int-script=path a --int-only \n");
        echo("--jexamxml=path, zmena cesty k .jar souboru jexamxml. Defaultne .jar soubor hleda v /pub/courses/ipp/jexamxml/jexamxml.jar na serveru merlin.fit.vutbr.cz\n");
        exit(0);
      }
      elseif (preg_match('/--directory=.*/',$argv[$i]) == 1) {
        $this->directoryPath = explode("=",$argv[$i]);
        $this->directoryPath = $this->directoryPath[1];
      }
      elseif ($argv[$i] == "--recursive") {
        $this->recursiveFlag = 1;
      }
      elseif ($argv[$i] == "--parse-only") {
        if ($this->intOnlyFlag == 1 || $this->parseScriptFlag == 1)  {
          exit(10);
        }
        $this->parseOnlyFlag = 1;
      }
      elseif ($argv[$i] == "--int-only") {
        if ($this->parseOnlyFlag == 1 || $this->parseScriptFlag == 1)  {
          exit(10);
        }       
        $this->intOnlyFlag = 1;
      }
      elseif (preg_match('/--parse-script=.*/',$argv[$i]) == 1) {
        if ($this->intOnlyFlag == 1) {
          exit(10);
        }
        $this->parseScriptPath = explode("=",$argv[$i]);
        $this->parseScriptPath = $this->parseScriptPath[1];
        $this->parseScriptFlag = 1;
      }
      elseif (preg_match('/--int-script=.*/',$argv[$i]) == 1) {
        if ($this->parseOnlyFlag == 1) {
          exit(10);
        }       
        $this->intScriptPath = explode("=",$argv[$i]);
        $this->intScriptPath = $this->intScriptPath[1];
        $this->intScriptFlag = 1;
      }
      elseif (preg_match('/--jexamxml=.*/',$argv[$i]) == 1) {
        $this->jexamxmlPath = explode("=",$argv[$i]);
        $this->jexamxmlPath = $this->jexamxmlPath[1];
      }
      else {
        exit(10);
      }
    }
  }
}

//trida generujici vysledky html soubor na standartni vystup
class htmlGenerator {

  //vygeneruje zacatek dokumentu
  public function headers() {
    echo "<style>
    div.sticky {
      position: fixed;
      top: 0;
      padding: 50px;
      font-size: 20px;
    }
    table, th, td {
      border: 1px solid black;
      padding: 5px;
    }
    table {
      border-spacing: 15px;
    }
    </style>";
    echo "<h1 align=\"center\" position= \"sticky\">IPPCODE20</h1>";
    echo "<h2 align=\"center\">Test.php</h1>";
  }
  
  //vygeneruje zacatek tabulky
  public function startTable($name) {
    echo "<table align=\"center\">";
    echo "<tr><td align = \"right\">Directory:</td><td align = \"left\">".$name."</td></tr>";
    echo "</table>";
    echo "<table align=\"center\">";
    echo  "<tr>
            <th>Test</th>
            <th>Return Code</th>
            <th>Sample Return Code</th>
            <th>Output</th>
            <th>Test result</th>            
          </tr>";
  }
  
  //ukonci tabulku
  public function endTable() {
    echo "</table>";
  }
  

  //vygeneruje novy radek tabulky
  public function tableRow($file,$returnCode,$sampleReturnCode,$diffMatch,$pass) {
    echo  "<tr>
            <td>".$file."</td>
            <td>".$returnCode."</td>
            <td>".$sampleReturnCode."</td>
            <td>".$diffMatch."</td>"; 
    if ($pass == 1) {
      echo "<td><font size='3' color='green'>PASSED TEST</font></td>";
    }
    else {
      echo "<td><font size='3' color='red'>FAILED TEST</font></td>";
    }
  }

  //vyeneruje souhrn testu
  public function generateSummary($failedCount,$passedCount) {
    $numberOfTests = $passedCount + $failedCount;
    echo "<div class=\"sticky\">";
    echo "<h1>FAILED COUNT:".$failedCount."</h1>";
    echo "<h1>PASSED COUNT:".$passedCount."</h1>";
    echo "<h1>TEST COUNT:".$numberOfTests."</h1>";
    echo "</div>";
  }
}

//trida, ktera prochazi slozky a spousti skripty parse.php a interpret.php
class DirectoryHandler {
  public $path;
  public $level = 0;
  public $tmpFile;
  public $recursiveFlag;
  public $mode;
  public $htmlGenerator;
  public $passedCount = 0;
  public $failedCount = 0;
  public $startTableFlag = 0;
  public $jexamxmlPath;
  public $interpetPath;
  public $parsePath;

  //pri volani konstruktoru se nastavi cesty souboru z argumentu popripade se nastavi na defaultni hodnoty
  public function __construct($path,$recursiveFlag,$mode,$htmlGenerator,$jexamxmlPath,$interpetPath,$parsePath) {
    $this->path = $path;
    $this->tmpFile = new TemporaryFile();
    $this->recursiveFlag = $recursiveFlag;
    $this->mode = $mode;
    $this->htmlGenerator = $htmlGenerator;
    $this->jexamxmlPath = $jexamxmlPath;
    $this->interpretPath = $interpetPath;
    $this->parsePath = $parsePath;
  }

  //hlavni cyklus skriptu ktery prochazi slozku po souborech, popripade v rekurzivnich rezimu prochazi zadany adresar
  public function goTroughDirectory($path = ".") {
    $ignore = array( 'cgi-bin', '.', '..' );
    $dh = @opendir($path);
    while( false !== ( $file = readdir( $dh ) ) ){
        if( !in_array( $file, $ignore ) ){
            if ($this->recursiveFlag == 1 && is_dir( "$this->path/$file" )) {
                    $this->htmlGenerator->startTable($file);
                    $this->startTableFlag = 1;
                    $this->goTroughDirectory( "$path/$file");
            }
            else {
              if ($this->startTableFlag == 0) {
                $this->htmlGenerator->startTable($path);
                $this->startTableFlag = 1;
              }

              //podle modu se vola odpovidajici funkce
              if ($this->mode == 0) { //both
                $this->modeBoth($file,$path);
              }
              elseif ($this->mode == 1) { //int only
                $this->modeIntOnly($file,$path);
              }
              elseif ($this->mode == 2) { //parse only
                $this->modeParseOnly($file,$path);
              }
            }
      }
    }
    closedir( $dh );
  }

  //mod both prvne spousti skript parse.php na soubor *.in a pote spousti skript interpret.py s vystupem parse.php
  //porovnava navratove hodnoty, popripade vystupy a zasila zpravu html generatoru 
  public function modeBoth($file,$path) {
    if (preg_match("/.src/",$file)) {
      $this->tmpFile->newFile();
      $pathOut =  $this->tmpFile->getFilePath();
      $src = $file;

      $rc = str_replace(".src",".rc",$file);
      if (!file_exists(($path."/".$rc))) {
        $rcFile = fopen($path."/".$rc,"w+");
        $txt = "0";
        fwrite($rcFile, $txt);
        fclose($rcFile);
      }

      $out = str_replace(".src",".out",$file);
      if (!file_exists($path."/".$out)) {
        $outFile = fopen($path."/".$out,"w");
        fclose($outFile);                    
      }
      
      $in = str_replace(".src",".in",$file);
      if (!file_exists($path."/".$in)) {
        $inFile = fopen($path."/".$in,"w");
        fclose($inFile);                  
      }

      $rcFlag = 0;
      $outFlag = 0;

      exec('php7.4  '.$this->parsePath.' < ' . $path . "/" . $src ,$parserOut,$parseReturn);
      $this->tmpFile->writeContent($parserOut);

      exec('python3.6 '.$this->interpretPath.' --source='.$this->tmpFile->getFilePath().' --input='.$path.'/'.$in, $interpretOut,$interpretReturn);
      
      $this->tmpFile->flushContent();
      $this->tmpFile->writeContent($interpretOut);

      $sampleReturn = file_get_contents($path . "/" . $rc);
      if ($interpretReturn == $sampleReturn) {
        $rcFlag = 1;
      }
      if ($sampleReturn == 0) {
        exec('diff ' . $this->tmpFile->getFilePath() . ' ' . $path."/".$out,$diffOut,$diffReturn);       
        $this->tmpFile->flushContent();    
        if ($diffReturn == 0) {
          $outFlag = 1;
        }
      }
      else {
        $outFlag = 1;
      }
      if ($rcFlag == 1 and $outFlag == 1) {
        $this->htmlGenerator->tableRow($file,$interpretReturn,$sampleReturn,"MATCH",1);
        $this->passedCount ++;
      }
      else {
        $this->htmlGenerator->tableRow($file,$interpretReturn,$sampleReturn,"DO NOT MATCH",0);
        $this->failedCount++;
      }
    }
  }

  //mod int-only  pousti skript interpret.py na soubor *.in
  // a porovnava navratove hodnoty, popripade vystupy a zasila zpravu html generatoru 
  public function modeIntOnly($file,$path) {
    if (preg_match("/.src/",$file)) {
      $this->tmpFile->newFile();
      $pathOut =  $this->tmpFile->getFilePath();
      $src = $file;

      $rc = str_replace(".src",".rc",$file);
      if (!file_exists(($path."/".$rc))) {
        $rcFile = fopen($path."/".$rc,"w+");
        $txt = "0";
        fwrite($rcFile, $txt);
        fclose($rcFile);
      }

      $out = str_replace(".src",".out",$file);
      if (!file_exists($path."/".$out)) {
        $outFile = fopen($path."/".$out,"w");
        fclose($outFile);                    
      }
      
      $in = str_replace(".src",".in",$file);
      if (!file_exists($path."/".$in)) {
        $inFile = fopen($path."/".$in,"w");
        fclose($inFile);                  
      }

      $rcFlag = 0;
      $outFlag = 0;

      exec('python3 '.$this->interpretPath.' --source='.$path.'/'.$src.' --input='.$path.'/'.$in, $interpretOut,$interpretReturn);
  
      $this->tmpFile->flushContent();
      $this->tmpFile->writeContent($interpretOut);

      $sampleReturn = file_get_contents($path . "/" . $rc);
      if ($interpretReturn == $sampleReturn) {
        $rcFlag = 1;
      }
      if ($sampleReturn == 0) {
        exec('diff ' . $this->tmpFile->getFilePath() . ' ' . $path."/".$out,$diffOut,$diffReturn);       
        $this->tmpFile->flushContent();    
        if ($diffReturn == 0) {
          $outFlag = 1;
        }
      }
      else {
        $outFlag = 1;
      }
      if ($rcFlag == 1 and $outFlag == 1) {
        $this->htmlGenerator->tableRow($file,$interpretReturn,$sampleReturn,"MATCH",1);
        $this->passedCount ++;
      }
      else {
        $this->htmlGenerator->tableRow($file,$interpretReturn,$sampleReturn,"DO NOT MATCH",0);
        $this->failedCount++;
      }
    }
  }

  //mod parse-only pousti skript interpret.py na soubor *.in
  // a porovnava navratove hodnoty, popripade vystupy pomoci nastroje jexam a zasila zpravu html generatoru 
  public function modeParseOnly($file,$path) {

    if (preg_match("/.src/",$file)) {
      $this->tmpFile->newFile();
      $pathOut =  $this->tmpFile->getFilePath();
      $src = $file;

      $rc = str_replace(".src",".rc",$file);
      if (!file_exists(($path."/".$rc))) {
        $rcFile = fopen($path."/".$rc,"w+");
        $txt = "0";
        fwrite($rcFile, $txt);
        fclose($rcFile);
      }

      $out = str_replace(".src",".out",$file);
      if (!file_exists($path."/".$out)) {
        $outFile = fopen($path."/".$out,"w");
        fclose($outFile);                    
      }
      
      $rcFlag = 0;
      $outFlag = 0;

      exec('php7.4  '.$this->parsePath.'  < ' . $path . "/" . $src ,$parserOut,$parseReturn);
      $this->tmpFile->writeContent($parserOut);

      $sampleReturn = file_get_contents($path . "/" . $rc);
      if ($parseReturn == $sampleReturn) {
        $rcFlag = 1;
      }
      if ($sampleReturn == 0) {
        exec('java -jar '.$this->jexamxmlPath.' '. $pathOut . ' ' . $path . "/" . $out,$dump,$jexamReturn);
        if ($jexamReturn == 0) {
          $outFlag = 1;
        }   
      }
      else {
        $outFlag = 1;
      }
      if ($rcFlag == 1 and $outFlag == 1) {
        $this->htmlGenerator->tableRow($file,$parseReturn,$sampleReturn,"MATCH",1);
        $this->passedCount ++;
      }
      else {
        $this->htmlGenerator->tableRow($file,$parseReturn,$sampleReturn,"DO NOT MATCH",0);
        $this->failedCount++;
      }
    }
  }
  
}

//trida pro praci s docasnym souborem
class TemporaryFile {
  private $file;
  public function newFile() {
    $this->file = tmpfile();
  }
  public function closeFile() {
    fclose($this->file);
  }
  public function flushContent() {
    $this->closeFile();
    $this->newFile();
  }
  public function getFilePath() {
    $metaDatas = stream_get_meta_data($this->file);
    return $metaDatas['uri'];    
  }
  public function writeContent($array) {
    foreach ($array as $value) {
      fwrite($this->file,$value);
      if (count($array) > 1) {
        fwrite($this->file,"\n");
      }
    }
  }
}
 ?>
