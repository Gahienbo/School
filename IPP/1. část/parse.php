<?php
  checkArguments($argv);
  $lineCounter = 0;
  $writer = constructWriter();
  while (FALSE !== ($line = fgets(STDIN))) { //main cycle of the script loads STDIN line by line
    $line = DeleteComment($line); //deltes comments from loaded line
    if ($line != NULL) {
        if ($lineCounter == 0) {
          checkHeader($line);
        }
        else {
          checkSyntax($line,$writer);
        }
      $lineCounter++;
    }
  }
  endElement($writer);
  echo ($writer->outputMemory()); //prints XML

  function deleteComment($line) { //function that deletes comments from lines
    if (strpos($line, '#') !== false) {
      $line = explode('#', $line); //splits line into two arrrays
      $line = $line[0]; //line without comment = $line[0]
      $line .= "\n"; // adds EOL
    }
    if ($line != "\n") {
      return $line;
    }
    else {
      return NULL;
    }
  }
  function checkHeader($line) {
    $line = strtolower($line); //.IPPcode20 is case insensitive
    if (preg_match('/^\s*\.ippcode20\b\s*$/',$line) != 1) {
      exit(21); //exit with header exit code
    }
  }
  function convertStringToArray($line) {
      $lineArr = preg_split("/[\s]+/", $line); //splits $line into array of single commands
      if ($lineArr[count($lineArr)-1] == "") {
        array_pop($lineArr); //pops last element of $lineArr which is always space
      }
      if (trim($lineArr[0]) == '') { //if there was a space at the start of the line it will be removed from array
        array_shift($lineArr);
      }
      $lineArr[0] = strtoupper($lineArr[0]); // instructions are case insensitive
      return $lineArr;
  }
  function checkSyntax($line,$writer) { //function that checks syntax
    $lineArr = convertStringToArray($line);
    switch ($lineArr[0]) {
      case 'MOVE': //var symb function generateIns($writer,$opCode,$arg1,$arg2,$arg3,$arr)
          generateIns($writer,'MOVE','var','symb',NULL,$lineArr);
        break;
      case 'CREATEFRAME':
        generateIns($writer,'CREATEFRAME',NULL,NULL,NULL,$lineArr);
        break;
      case 'PUSHFRAME':
        generateIns($writer,'PUSHFRAME',NULL,NULL,NULL,$lineArr);

        break;
      case 'POPFRAME':
        generateIns($writer,'POPFRAME',NULL,NULL,NULL,$lineArr);
        break;
      case 'DEFVAR': //var
        generateIns($writer,'DEFVAR','var',NULL,NULL,$lineArr);
        break;
      case 'CALL': //label
        generateIns($writer,'CALL','label',NULL,NULL,$lineArr);
        break;
      case 'RETURN':
        generateIns($writer,'RETURN',NULL,NULL,NULL,$lineArr);
        break;
      case 'PUSHS': //symb
        generateIns($writer,'PUSHS','symb',NULL,NULL,$lineArr);
        break;
      case 'POPS': //var
        generateIns($writer,'POPS','var',NULL,NULL,$lineArr);
        break;
      case 'ADD': //var symb1 symb2
        generateIns($writer,'ADD','var','symb','symb',$lineArr);
        break;
      case 'SUB'://var symb1 symb2
        generateIns($writer,'SUB','var','symb','symb',$lineArr);
        break;
      case 'MUL'://var symb1 symb2
        generateIns($writer,'MUL','var','symb','symb',$lineArr);
        break;
      case 'IDIV'://var symb1 symb2
        generateIns($writer,'IDIV','var','symb','symb',$lineArr);
        break;
      case 'LT'://var symb1 symb2
        generateIns($writer,'LT','var','symb','symb',$lineArr);
        break;
      case 'GT'://var symb1 symb2
        generateIns($writer,'GT','var','symb','symb',$lineArr);
        break;
      case 'EQ'://var symb1 symb2
        generateIns($writer,'EQ','var','symb','symb',$lineArr);
        break;
      case 'AND'://var symb1 symb2
        generateIns($writer,'AND','var','symb','symb',$lineArr);
        break;
      case 'OR'://var symb1 symb2
        generateIns($writer,'OR','var','symb','symb',$lineArr);
        break;
      case 'NOT'://var symb1 symb2
        generateIns($writer,'NOT','var','symb','symb',$lineArr);
        break;
      case 'INT2CHAR'://var symb
        generateIns($writer,'INT2CHAR','var','symb',NULL,$lineArr);
        break;
      case 'STRI2INT'://var symb1 symb2
        generateIns($writer,'STRI2INT','var','symb','symb',$lineArr);
        break;
      case 'READ': //var type
        generateIns($writer,'READ','var','type',NULL,$lineArr);
        break;
      case 'WRITE'://symb
        generateIns($writer,'WRITE','symb',NULL,NULL,$lineArr);
        break;
      case 'CONCAT'://var symb1 symb2
        generateIns($writer,'CONCAT','var','symb','symb',$lineArr);
        break;
      case 'STRLEN'://var symb
        generateIns($writer,'STRLEN','var','symb',NULL,$lineArr);
        break;
      case 'GETCHAR'://var symb1 symb2
        generateIns($writer,'GETCHAR','var','symb','symb',$lineArr);
        break;
      case 'SETCHAR'://var symb1 symb2
        generateIns($writer,'SETCHAR','var','symb','symb',$lineArr);
        break;
      case 'TYPE'://var symb
        generateIns($writer,'TYPE','var','symb',NULL,$lineArr);
        break;
      case 'LABEL'://label
        generateIns($writer,'LABEL','label',NULL,NULL,$lineArr);
        break;
      case 'JUMP'://label
        generateIns($writer,'JUMP','label',NULL, NULL,$lineArr);
        break;
      case 'JUMPIFEQ'://label symb1 symb2
        generateIns($writer,'JUMPIFEQ','label','symb','symb',$lineArr);
        break;
      case 'JUMPIFNEQ'://label symb1 symb2
        generateIns($writer,'JUMPIFNEQ','label','symb','symb',$lineArr);
        break;
      case 'EXIT'://symb
        generateIns($writer,'EXIT','symb',NULL,NULL,$lineArr);
        break;
      case 'DPRINT'://symb
        generateIns($writer,'DPRINT','symb',NULL,NULL,$lineArr);
      case 'BREAK':
        generateIns($writer,'BREAK',NULL,NULL,NULL,$lineArr);
        break;
      default:
        exit(22);
        break;
    }
  }
  function checkVar($var) {
    if (preg_match('/^(LF@|TF@|GF@)[a-zA-Z,-,$,&,%,*,!,?]+[a-zA-Z0-9,-,$,&,%,*,!,?]*$/',$var) == 1) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkType($type) { //int, string, bool
    if ($type == "int" OR $type == "string" OR $type == "bool" OR $type == "nill") {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkLabel($label) {
    if (preg_match('/^[a-zA-Z,\-,_,$,&,%,*,!,?][a-zA-Z0-9,-,$,&,%,*,!,?]*$/',$label) == 1) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkConstant($constant) {
    if (preg_match('/^int@/',$constant) == 1) {
      return(checkInt($constant));
    }
    elseif (preg_match('/^bool@/',$constant) == 1) {
      return(checkBool($constant));
    }
    elseif (preg_match('/^string@/',$constant) == 1) {
      return(checkString($constant));
    }
    elseif (preg_match('/^nil@nil\b$/',$constant) == 1) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkInt($int) {
    if (preg_match('/int@(\+|-|)[0-9]+$/',$int) == 1) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkBool($bool) {
    if (preg_match('/bool@true|bool@false/',$bool) == 1) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  function checkString($string) {
    //echo $string;
    if (preg_match('/^string@[\w]*$/',$string) == 1) {
      //echo "SUCESS\n";
      return TRUE;
    }
    else {
      //echo "NOPE\n";
      return FALSE;
    }
  }
  function checkArguments($argv) //function that checks arguments
  {
    if (isset($argv[1])) {
      if ($argv[1] == '--help') {
        if (!isset($argv[2])) {
            echo "Skript typu filtr (parse.php v jazyce PHP 7.4) načte ze standardního vstupu zdrojový kód v IPP-code20, zkontroluje lexikální a syntaktickou správnost kódu a vypíše na standardnívýstup XML reprezentaci programu \n";
          exit(0);
        }
        else {
          exit(10); // wrong parameter exit code
        }
      }
    }
  }
  function varOrConstant($symbol,$content) {
    if (checkVar($content) == TRUE) {
      return "var";
    }
    elseif (checkConstant($content) == TRUE) {
      $constantArr = explode("@",$content);
      return $constantArr[0];
    }
  }
  function constructWriter() {
    $writer = new XMLWriter();
    $writer->openMemory();

    //generating header
    $writer->startDocument('1.0','UTF-8');
    $writer->setIndent(true);
    //generates program element
    $writer->startElement('program');
    addAttribute($writer,'language','IPPcode20');

    return $writer;
  }
  function generateIns($writer,$opCode,$arg1,$arg2,$arg3,$arr) {
    global $lineCounter;
    $writer->startElement('instruction');
    $writer->writeAttribute('order', $lineCounter);
    $writer->writeAttribute('opcode',$opCode);
    if ($arg1) {
      if (array_key_exists(1, $arr)) {
        generateArg($writer,"arg1",$arg1,$arr[1]);
      }
      else {
        //echo "exiting here 1";
        exit(23);
      }
    }
    else {
      if (array_key_exists(1, $arr)) {
        exit(23);
      }
    }
    if ($arg2) {
      if (array_key_exists(2, $arr)) {
        generateArg($writer,"arg2",$arg2,$arr[2]);
      }
      else {
        //echo "exiting here 2";
        exit(23);
      }
    }
    else {
      if (array_key_exists(2, $arr)) {
        exit(23);
      }
    }
    if ($arg3) {
      if (array_key_exists(3, $arr)) {
        generateArg($writer,"arg3",$arg3,$arr[3]);
      }
      else {
        //echo "exiting here 3";

        exit(23);
      }
    }
    else {
      if (array_key_exists(3, $arr)) {
        exit(23);
      }
    }
    endElement($writer);
  }
  function generateInstruction($writer,$opCode) {
    global $lineCounter;
    $writer->startElement('instruction');
    $writer->writeAttribute('order', $lineCounter);
    $writer->writeAttribute('opcode',$opCode);

  }
  function generateArg($writer,$name,$type,$content) {
    $writer->startElement($name);
    if ($type == "var") {
      if (checkVar($content) == TRUE) {
        $writer->writeAttribute('type', $type);
        $writer->text($content);
      }
      else {
        //echo "exiting here 4";

        exit(23);
      }
    }
    elseif ($type == "label") {
        if (checkLabel($content) == TRUE) {
          $writer->writeAttribute('type', $type);
          $writer->text($content);
        }
        else {
          exit(23);
        }
    }
    elseif ($type == "symb") {
      if (checkVar($content) == TRUE or checkConstant($content) == TRUE) {
        $type = varOrConstant($type,$content);
        if ($type == "int" or $type == "bool" or $type == "string" or $type == "nil") { //int, bool, string,nil
          $constantExp = explode("@",$content);
          $content = $constantExp[1];
        }
        $writer->writeAttribute('type', $type);
        $writer->text($content);
      }
      else {
        //echo "exiting here 5";

        exit(23);
      }
    }
    elseif ($type == "type") {
      if (checkType($content) == TRUE) {
        $writer->writeAttribute('type', $type);
        $writer->text($content);
      }
      else {
        //echo "exiting here 6";

        exit(23);
      }
    }
    $writer->endElement();
  }
  function endElement($writer) {
    $writer->endElement();
  }
  function addAttribute($writer,$attribute,$attributeValue) {
    $writer->writeAttribute($attribute,$attributeValue);
  }
 ?>
