/*!
 * @file
 * @brief This file contains implementation of gpu
 *
 * @author Tomáš Milet, imilet@fit.vutbr.cz
 */

#include <student/gpu.hpp>


/// \addtogroup gpu_init
/// @{

/**
 * @brief Constructor of GPU
 */
GPU::GPU(){
  /// \todo Zde můžete alokovat/inicializovat potřebné proměnné grafické karty
}

/**
 * @brief Destructor of GPU
 */
GPU::~GPU(){
  /// \todo Zde můžete dealokovat/deinicializovat grafickou kartu

}

/// @}

/** \addtogroup buffer_tasks 01. Implementace obslužných funkcí pro buffery
 * @{
 */

/**
 * @brief This function allocates buffer on GPU.
 *
 * @param size size in bytes of new buffer on GPU.
 *
 * @return unique identificator of the buffer
 */

BufferID GPU::createBuffer(uint64_t size) { 
  /// \todo Tato funkce by měla na grafické kartě vytvořit buffer dat.<br>
  /// Velikost bufferu je v parameteru size (v bajtech).<br>
  /// Funkce by měla vrátit unikátní identifikátor identifikátor bufferu.<br>
  /// Na grafické kartě by mělo být možné alkovat libovolné množství bufferů o libovolné velikosti.<br>
  //buffer *newBuffer = malloc(sizeof(buffer));
  auto buffer = std::vector<uint8_t>(size_t(size));
  BufferID id = bufferCount;
  bufferCount++;
  bufferMap.emplace(id,buffer);
  return id;
}

/**
 * @brief This function frees allocated buffer on GPU.
 *
 * @param buffer buffer identificator
 */
void GPU::deleteBuffer(BufferID buffer) {
  /// \todo Tato funkce uvolní buffer na grafické kartě.
  /// Buffer pro smazání je vybrán identifikátorem v parameteru "buffer".
  /// Po uvolnění bufferu je identifikátor volný a může být znovu použit při vytvoření nového bufferu.
  if (bufferMap.find(buffer) != bufferMap.end()) bufferMap.erase(buffer);
}

/**
 * @brief This function uploads data to selected buffer on the GPU
 *
 * @param buffer buffer identificator
 * @param offset specifies the offset into the buffer's data
 * @param size specifies the size of buffer that will be uploaded
 * @param data specifies a pointer to new data
 */
void GPU::setBufferData(BufferID buffer, uint64_t offset, uint64_t size, void const* data) {
  /// \todo Tato funkce nakopíruje data z cpu na "gpu".<br>
  /// Data by měla být nakopírována do bufferu vybraného parametrem "buffer".<br>
  /// Parametr size určuje, kolik dat (v bajtech) se překopíruje.<br>
  /// Parametr offset určuje místo v bufferu (posun v bajtech) kam se data nakopírují.<br>
  /// Parametr data obsahuje ukazatel na data na cpu pro kopírování.<br>
  if (bufferMap.find(buffer) != bufferMap.end()) {
    auto tmpBuffer = bufferMap.find(buffer);
    std::copy((int8_t*)data, (int8_t*)data + size, (*tmpBuffer).second.begin() + offset);
  }
}

/**
 * @brief This function downloads data from GPU.
 *
 * @param buffer specfies buffer
 * @param offset specifies the offset into the buffer from which data will be returned, measured in bytes. 
 * @param size specifies data size that will be copied
 * @param data specifies a pointer to the location where buffer data is returned. 
 */
void GPU::getBufferData(BufferID buffer,
                        uint64_t offset,
                        uint64_t size,
                        void*    data)
{
  /// \todo Tato funkce vykopíruje data z "gpu" na cpu.
  /// Data by měla být vykopírována z bufferu vybraného parametrem "buffer".<br>
  /// Parametr size určuje kolik dat (v bajtech) se překopíruje.<br>
  /// Parametr offset určuje místo v bufferu (posun v bajtech) odkud se začne kopírovat.<br>
  /// Parametr data obsahuje ukazatel, kam se data nakopírují.<br>
  if (bufferMap.find(buffer) != bufferMap.end()) {
    auto tmpBuffer = bufferMap.find(buffer);
    std::copy((*tmpBuffer).second.begin() + offset, (*tmpBuffer).second.begin() + offset + size, (int8_t*)data);
  }
}

/**
 * @brief This function tests if buffer exists
 *
 * @param buffer selected buffer id
 *
 * @return true if buffer points to existing buffer on the GPU.
 */
bool GPU::isBuffer(BufferID buffer) { 
  /// \todo Tato funkce by měla vrátit true pokud buffer je identifikátor existující bufferu.<br>
  /// Tato funkce by měla vrátit false, pokud buffer není identifikátor existujícího bufferu. (nebo bufferu, který byl smazán).<br>
  /// Pro emptyId vrací false.<br>
  if (bufferMap.find(buffer) != bufferMap.end()) {
    return true;
  }
  return false; 
}

/// @}

/**
 * \addtogroup vertexpuller_tasks 02. Implementace obslužných funkcí pro vertex puller
 * @{
 */

/**
 * @brief This function creates new vertex puller settings on the GPU,
 *
 * @return unique vertex puller identificator
 */
ObjectID GPU::createVertexPuller     (){
  /// \todo Tato funkce vytvoří novou práznou tabulku s nastavením pro vertex puller.<br>
  /// Funkce by měla vrátit identifikátor nové tabulky.
  /// Prázdná tabulka s nastavením neobsahuje indexování a všechny čtecí hlavy jsou vypnuté.
  vertexTable table = {};
  VertexPullerID id = vertexCount;
  vertexCount++;
  vertexPullers.emplace(id, table);
  return id;
  
}

/**
 * @brief This function deletes vertex puller settings
 *
 * @param vao vertex puller identificator
 */
void     GPU::deleteVertexPuller     (VertexPullerID vao){
  /// \todo Tato funkce by měla odstranit tabulku s nastavení pro vertex puller.<br>
  /// Parameter "vao" obsahuje identifikátor tabulky s nastavením.<br>
  /// Po uvolnění nastavení je identifiktátor volný a může být znovu použit.<br>
  if (vertexPullers.find(vao) != vertexPullers.end()) vertexPullers.erase(vao);
}

/**
 * @brief This function sets one vertex puller reading head.
 *
 * @param vao identificator of vertex puller
 * @param head id of vertex puller head
 * @param type type of attribute
 * @param stride stride in bytes
 * @param offset offset in bytes
 * @param buffer id of buffer
 */
void     GPU::setVertexPullerHead    (VertexPullerID vao,uint32_t head,AttributeType type,uint64_t stride,uint64_t offset,BufferID buffer){
  /// \todo Tato funkce nastaví jednu čtecí hlavu vertex pulleru.<br>
  /// Parametr "vao" vybírá tabulku s nastavením.<br>
  /// Parametr "head" vybírá čtecí hlavu vybraného vertex pulleru.<br>
  /// Parametr "type" nastaví typ atributu, který čtecí hlava čte. Tímto se vybere kolik dat v bajtech se přečte.<br>
  /// Parametr "stride" nastaví krok čtecí hlavy.<br>
  /// Parametr "offset" nastaví počáteční pozici čtecí hlavy.<br>
  /// Parametr "buffer" vybere buffer, ze kterého bude čtecí hlava číst.<br>

  if (vertexPullers.find(vao) != vertexPullers.end()) {
    auto tmpTable = vertexPullers.find(vao);
    auto& vertexHead = tmpTable->second.vertexHeadArray;
    vertexHead.at(head) = {(size_t)offset,stride,type,buffer };
  }

}

/**
 * @brief This function sets vertex puller indexing.
 *
 * @param vao vertex puller id
 * @param type type of index
 * @param buffer buffer with indices
 */
void     GPU::setVertexPullerIndexing(VertexPullerID vao,IndexType type,BufferID buffer){
  /// \todo Tato funkce nastaví indexování vertex pulleru.
  /// Parametr "vao" vybírá tabulku s nastavením.<br>
  /// Parametr "type" volí typ indexu, který je uložený v bufferu.<br>
  /// Parametr "buffer" volí buffer, ve kterém jsou uloženy indexy.<br>

  if (vertexPullers.find(vao) != vertexPullers.end()) {
    auto tmpTable = vertexPullers.find(vao);
    (*tmpTable).second.indexEnable = true;
    (*tmpTable).second.indexBuffer.id = buffer;
    (*tmpTable).second.indexBuffer.indexType = type;
  }

}

/**
 * @brief This function enables vertex puller's head.
 *
 * @param vao vertex puller 
 * @param head head id
 */
void     GPU::enableVertexPullerHead (VertexPullerID vao,uint32_t head){
  /// \todo Tato funkce povolí čtecí hlavu daného vertex pulleru.<br>
  /// Pokud je čtecí hlava povolena, hodnoty z bufferu se budou kopírovat do atributu vrcholů vertex shaderu.<br>
  /// Parametr "vao" volí tabulku s nastavením vertex pulleru (vybírá vertex puller).<br>
  /// Parametr "head" volí čtecí hlavu.<br>
  if (vertexPullers.find(vao) != vertexPullers.end()) {
    auto tmpTable = vertexPullers.find(vao);
    auto& vertexHead = tmpTable->second.vertexHeadArray;
    vertexHead.at(head).enabled = true;
  }
}

/**
 * @brief This function disables vertex puller's head
 *
 * @param vao vertex puller id
 * @param head head id
 */
void     GPU::disableVertexPullerHead(VertexPullerID vao,uint32_t head){
  /// \todo Tato funkce zakáže čtecí hlavu daného vertex pulleru.<br>
  /// Pokud je čtecí hlava zakázána, hodnoty z bufferu se nebudou kopírovat do atributu vrcholu.<br>
  /// Parametry "vao" a "head" vybírají vertex puller a čtecí hlavu.<br>
  if (vertexPullers.find(vao) != vertexPullers.end()) {
    auto tmpTable = vertexPullers.find(vao);
    auto& vertexHead = tmpTable->second.vertexHeadArray;
    vertexHead.at(head).enabled = false;
  }
}

/**
 * @brief This function selects active vertex puller.
 *
 * @param vao id of vertex puller
 */
void     GPU::bindVertexPuller       (VertexPullerID vao){
  /// \todo Tato funkce aktivuje nastavení vertex pulleru.<br>
  /// Pokud je daný vertex puller aktivován, atributy z bufferů jsou vybírány na základě jeho nastavení.<br>
  if (vertexPullers.find(vao) != vertexPullers.end()) {
    auto tmpTable = vertexPullers.find(vao);
    activeVertex = &tmpTable->second;
  }
}

/**
 * @brief This function deactivates vertex puller.
 */
void     GPU::unbindVertexPuller     (){
  /// \todo Tato funkce deaktivuje vertex puller.
  /// To většinou znamená, že se vybere neexistující "emptyID" vertex puller.
  activeVertex = nullptr;
}

/**
 * @brief This function tests if vertex puller exists.
 *
 * @param vao vertex puller
 *
 * @return true, if vertex puller "vao" exists
 */
bool     GPU::isVertexPuller         (VertexPullerID vao){
  /// \todo Tato funkce otestuje, zda daný vertex puller existuje.
  /// Pokud ano, funkce vrací true.
  if (vertexPullers.find(vao) != vertexPullers.end()) {
    return true;
  }
  return false;
}

/// @}

/** \addtogroup program_tasks 03. Implementace obslužných funkcí pro shader programy
 * @{
 */

/**
 * @brief This function creates new shader program.
 *
 * @return shader program id
 */
ProgramID        GPU::createProgram         (){
  /// \todo Tato funkce by měla vytvořit nový shader program.<br>
  /// Funkce vrací unikátní identifikátor nového proramu.<br>
  /// Program je seznam nastavení, které obsahuje: ukazatel na vertex a fragment shader.<br>
  /// Dále obsahuje uniformní proměnné a typ výstupních vertex attributů z vertex shaderu, které jsou použity pro interpolaci do fragment atributů.<br>
  Program program = {};
  ProgramID id = programCount;
  programCount++;
  programs.emplace(id, program);
  return id;
}

/**
 * @brief This function deletes shader program
 *
 * @param prg shader program id
 */
void             GPU::deleteProgram         (ProgramID prg){
  /// \todo Tato funkce by měla smazat vybraný shader program.<br>
  /// Funkce smaže nastavení shader programu.<br>
  /// Identifikátor programu se stane volným a může být znovu využit.<br>
  if (programs.find(prg) != programs.end()) programs.erase(prg);

}

/**
 * @brief This function attaches vertex and frament shader to shader program.
 *
 * @param prg shader program
 * @param vs vertex shader 
 * @param fs fragment shader
 */
void             GPU::attachShaders         (ProgramID prg,VertexShader vs,FragmentShader fs){
  /// \todo Tato funkce by měla připojít k vybranému shader programu vertex a fragment shader.
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.vertexShader = vs;
    tmp->second.fragmentShader = fs;
  }
}

/**
 * @brief This function selects which vertex attributes should be interpolated during rasterization into fragment attributes.
 *
 * @param prg shader program
 * @param attrib id of attribute
 * @param type type of attribute
 */
void             GPU::setVS2FSType          (ProgramID prg,uint32_t attrib,AttributeType type){
  /// \todo tato funkce by měla zvolit typ vertex atributu, který je posílán z vertex shaderu do fragment shaderu.<br>
  /// V průběhu rasterizace vznikají fragment.<br>
  /// Fragment obsahují fragment atributy.<br>
  /// Tyto atributy obsahují interpolované hodnoty vertex atributů.<br>
  /// Tato funkce vybere jakého typu jsou tyto interpolované atributy.<br>
  /// Bez jakéhokoliv nastavení jsou atributy prázdne AttributeType::EMPTY<br>
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.attributes.at(attrib) = type;
  }

}

/**
 * @brief This function actives selected shader program
 *
 * @param prg shader program id
 */
void             GPU::useProgram            (ProgramID prg){
  /// \todo tato funkce by měla vybrat aktivní shader program.
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    activeProgram = &tmp->second;
  }
}

/**
 * @brief This function tests if selected shader program exists.
 *
 * @param prg shader program
 *
 * @return true, if shader program "prg" exists.
 */
bool             GPU::isProgram             (ProgramID prg){
  /// \todo tato funkce by měla zjistit, zda daný program existuje.<br>
  /// Funkce vráti true, pokud program existuje.<br>
  if (programs.find(prg) != programs.end()) {
    return true;
  }
   return false;
}

/**
 * @brief This function sets uniform value (1 float).
 *
 * @param prg shader program
 * @param uniformId id of uniform value (number of uniform values is stored in maxUniforms variable)
 * @param d value of uniform variable
 */
void             GPU::programUniform1f      (ProgramID prg,uint32_t uniformId,float     const&d){
  /// \todo tato funkce by měla nastavit uniformní proměnnou shader programu.<br>
  /// Parametr "prg" vybírá shader program.<br>
  /// Parametr "uniformId" vybírá uniformní proměnnou. Maximální počet uniformních proměnných je uložen v programné \link maxUniforms \endlink.<br>
  /// Parametr "d" obsahuje data (1 float).<br>
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.uniformTable.uniform[uniformId].v1 = d;
    //tmp->second.uniform.v1 = d;
  }
}

/**
 * @brief This function sets uniform value (2 float).
 *
 * @param prg shader program
 * @param uniformId id of uniform value (number of uniform values is stored in maxUniforms variable)
 * @param d value of uniform variable
 */
void             GPU::programUniform2f      (ProgramID prg,uint32_t uniformId,glm::vec2 const&d){
  /// \todo tato funkce dělá obdobnou věc jako funkce programUniform1f.<br>
  /// Místo 1 floatu nahrává 2 floaty.
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.uniformTable.uniform[uniformId].v2 = d;
    //tmp->second.uniform.v2 = d;
  }
}

/**
 * @brief This function sets uniform value (3 float).
 *
 * @param prg shader program
 * @param uniformId id of uniform value (number of uniform values is stored in maxUniforms variable)
 * @param d value of uniform variable
 */
void             GPU::programUniform3f      (ProgramID prg,uint32_t uniformId,glm::vec3 const&d){
  /// \todo tato funkce dělá obdobnou věc jako funkce programUniform1f.<br>
  /// Místo 1 floatu nahrává 3 floaty.
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.uniformTable.uniform[uniformId].v3 = d;
    //tmp->second.uniform.v3 = d;
  }
}

/**
 * @brief This function sets uniform value (4 float).
 *
 * @param prg shader program
 * @param uniformId id of uniform value (number of uniform values is stored in maxUniforms variable)
 * @param d value of uniform variable
 */
void             GPU::programUniform4f      (ProgramID prg,uint32_t uniformId,glm::vec4 const&d){
  /// \todo tato funkce dělá obdobnou věc jako funkce programUniform1f.<br>
  /// Místo 1 floatu nahrává 4 floaty.
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.uniformTable.uniform[uniformId].v4 = d;

    //tmp->second.uniform.v4 = d;
  }
}

/**
 * @brief This function sets uniform value (4 float).
 *
 * @param prg shader program
 * @param uniformId id of uniform value (number of uniform values is stored in maxUniforms variable)
 * @param d value of uniform variable
 */
void             GPU::programUniformMatrix4f(ProgramID prg,uint32_t uniformId,glm::mat4 const&d){
  /// \todo tato funkce dělá obdobnou věc jako funkce programUniform1f.<br>
  /// Místo 1 floatu nahrává matici 4x4 (16 floatů).
  if (programs.find(prg) != programs.end()) {
    auto tmp = programs.find(prg);
    tmp->second.uniformTable.uniform[uniformId].m4 = d;

    //tmp->second.uniform.m4 = d;
  }
}

/// @}





/** \addtogroup framebuffer_tasks 04. Implementace obslužných funkcí pro framebuffer
 * @{
 */

/**
 * @brief This function creates framebuffer on GPU.
 *
 * @param width width of framebuffer
 * @param height height of framebuffer
 */
void GPU::createFramebuffer      (uint32_t width,uint32_t height){
  /// \todo Tato funkce by měla alokovat framebuffer od daném rozlišení.<br>
  /// Framebuffer se skládá z barevného a hloukového bufferu.<br>
  /// Buffery obsahují width x height pixelů.<br>
  /// Barevný pixel je složen z 4 x uint8_t hodnot - to reprezentuje RGBA barvu.<br>
  /// Hloubkový pixel obsahuje 1 x float - to reprezentuje hloubku.<br>
  /// Nultý pixel framebufferu je vlevo dole.<br>
  const uint32_t size = width * height;
  frameBuffer.width = width;
  frameBuffer.height = height;

  frameBuffer.colorBuffer = new uint8_t[4*sizeof(uint8_t)*width * height];
  frameBuffer.depthBuffer = new float[sizeof(float)*width * height];


  for (size_t i = 0; i < width*height; i++)
  {
    frameBuffer.depthBuffer[i] = 2;
  }
  for (size_t i = 0; i < width*height*4; i+=4)
  {
    frameBuffer.colorBuffer[i] = 0;
    frameBuffer.colorBuffer[i+1] = 0;
    frameBuffer.colorBuffer[i+2] = 0;
    frameBuffer.colorBuffer[i+3] = 0;
  }
}


/**
 * @brief This function deletes framebuffer.
 */
void GPU::deleteFramebuffer      (){
  /// \todo tato funkce by měla dealokovat framebuffer.
  delete(frameBuffer.colorBuffer);
  delete(frameBuffer.depthBuffer);
}

/**
 * @brief This function resizes framebuffer.
 *
 * @param width new width of framebuffer
 * @param height new heght of framebuffer
 */
void     GPU::resizeFramebuffer(uint32_t width,uint32_t height){
  createFramebuffer(width, height);
  /// \todo Tato funkce by měla změnit velikost framebuffer.
}

/**
 * @brief This function returns pointer to color buffer.
 *
 * @return pointer to color buffer
 */
uint8_t* GPU::getFramebufferColor  (){
  /// \todo Tato funkce by měla vrátit ukazatel na začátek barevného bufferu.<br>
  return frameBuffer.colorBuffer;
}

/**
 * @brief This function returns pointer to depth buffer.
 *
 * @return pointer to dept buffer.
 */
float* GPU::getFramebufferDepth    (){
  return frameBuffer.depthBuffer;
}

/**
 * @brief This function returns width of framebuffer
 *
 * @return width of framebuffer
 */
uint32_t GPU::getFramebufferWidth (){
  /// \todo Tato funkce by měla vrátit šířku framebufferu.
  return frameBuffer.width;
}

/**
 * @brief This function returns height of framebuffer.
 *
 * @return height of framebuffer
 */
uint32_t GPU::getFramebufferHeight(){
  /// \todo Tato funkce by měla vrátit výšku framebufferu.
  return frameBuffer.height;
}

/// @}

/** \addtogroup draw_tasks 05. Implementace vykreslovacích funkcí
 * Bližší informace jsou uvedeny na hlavní stránce dokumentace.
 * @{
 */

/**
 * @brief This functino clears framebuffer.
 *
 * @param r red channel
 * @param g green channel
 * @param b blue channel
 * @param a alpha channel
 */
void            GPU::clear                 (float r,float g,float b,float a){
  /// \todo Tato funkce by měla vyčistit framebuffer.<br>
  /// Barevný buffer vyčistí na barvu podle parametrů r g b a (0 - nulová intenzita, 1 a větší - maximální intenzita).<br>
  /// (0,0,0) - černá barva, (1,1,1) - bílá barva.<br>
  /// Hloubkový buffer nastaví na takovou hodnotu, která umožní rasterizaci trojúhelníka, který leží v rámci pohledového tělesa.<br>
  /// Hloubka by měla být tedy větší než maximální hloubka v NDC (normalized device coordinates).<br>
  for (size_t i = 0; i < frameBuffer.width*frameBuffer.height; i++)
  {
    frameBuffer.depthBuffer[i] = 2;
  }
  for (size_t i = 0; i < frameBuffer.width*frameBuffer.height * 4; i += 4)
  {
    frameBuffer.colorBuffer[i] = r*255; 
    frameBuffer.colorBuffer[i + 1] = g*255;
    frameBuffer.colorBuffer[i + 2] = b*255;
    frameBuffer.colorBuffer[i + 3] = a*255;
  }

}

InVertex GPU::pullVertex(int invocationCount) {

  //navratovy InVertex
  InVertex returnVertex;
  if (activeVertex->indexEnable)
  {
    std::vector<uint32_t>tmpId;
    tmpId.resize((int)activeVertex->indexBuffer.indexType);
    getBufferData(activeVertex->indexBuffer.id, activeVertex->indexBuffer.offset+invocationCount* (int)activeVertex->indexBuffer.indexType,(int)activeVertex->indexBuffer.indexType, tmpId.data());
    returnVertex.gl_VertexID = *tmpId.data();
  }
  else {
    returnVertex.gl_VertexID = invocationCount;
  }
  //cyklus pres vsechny hlavy aktivniho vertexPulleru
  for (size_t i = 0; i < activeVertex->vertexHeadArray.size(); i++)
  {
    //pokud je hlava povolena prectou se data
    if (activeVertex->vertexHeadArray[i].enabled)
    {
      int dataSize = (int)activeVertex->vertexHeadArray[i].size;

      std::vector<Attribute>tmpData;
      tmpData.resize(sizeof(float)*dataSize);

      if (activeVertex->indexEnable)
      {
        getBufferData(activeVertex->vertexHeadArray[i].bufferID,activeVertex->vertexHeadArray[i].offset + (returnVertex.gl_VertexID*activeVertex->vertexHeadArray[i].stride), sizeof(float)*dataSize, tmpData.data());
        returnVertex.attributes[i] = *tmpData.data();

      }
      else {
        getBufferData(activeVertex->vertexHeadArray[i].bufferID, activeVertex->vertexHeadArray[i].offset+invocationCount* activeVertex->vertexHeadArray[i].stride, sizeof(float)*dataSize, tmpData.data());
        returnVertex.attributes[i] = *tmpData.data();
      }
    }
  }

  return returnVertex;
}

int GPU::isSeen(TrianglePoint point) {
  if (point.c.gl_Position.x != INFINITY && point.c.gl_Position.z != INFINITY && point.c.gl_Position.y != INFINITY)
  {
    if (-point.c.gl_Position.w <= point.c.gl_Position.z)
    {
      return 1;
    }
  }
  return 0; //bod neni videt
}

OutVertex GPU::calculateCutPoint(OutVertex A, OutVertex B) {
  glm::vec4 a = A.gl_Position;
  glm::vec4 b = B.gl_Position;
  OutVertex returnVertex; 

  float tBottom = (b.w - a.w + b.z - a.z); 
  float t = (-a.w - a.z) / tBottom;
  
  returnVertex.gl_Position = a + t * (b - a);

  for (size_t i = 0; i < 16; i++)
  {
    returnVertex.attributes[i].v1 = A.attributes[i].v1 + t *(B.attributes[i].v1 - A.attributes[i].v1);
    returnVertex.attributes[i].v2 = A.attributes[i].v2 + t * (B.attributes[i].v2 - A.attributes[i].v2);
    returnVertex.attributes[i].v3 = A.attributes[i].v3 + t * (B.attributes[i].v3 - A.attributes[i].v3);
    returnVertex.attributes[i].v4 = A.attributes[i].v4 + t * (B.attributes[i].v4 - A.attributes[i].v4);
  }

  return returnVertex;
}

void GPU::construct2NewTriangles(OutVertex* newTriangle1Point1, OutVertex* newTriangle1Point2, OutVertex* newTriangle2Point1,OutVertex notSeen,OutVertex b,OutVertex c) {

  OutVertex point1 = calculateCutPoint(b, notSeen);
  OutVertex point2 = calculateCutPoint(c, notSeen);
  int test = (int)point1.gl_Position.x;

  *newTriangle1Point1 = point1;
  *newTriangle1Point2 = point2;

  *newTriangle2Point1 = point1;
}

void GPU::constructNewTriangle(OutVertex* newTriangle1Point1, OutVertex* newTriangle1Point2, OutVertex notSeen, OutVertex b, OutVertex c) {
  OutVertex point1 = calculateCutPoint(b, notSeen);
  OutVertex point2 = calculateCutPoint(c, notSeen);

  *newTriangle1Point1 = point1;
  *newTriangle1Point2 = point2;
}

std::array<float,3> GPU::calculateBarycentric(Triangle triangle, float x, float y) {
  std::array<float, 3> barycentrics;
  
  float lambda1Up = (triangle.pointArray[1].c.gl_Position.y - triangle.pointArray[2].c.gl_Position.y) * (x - triangle.pointArray[2].c.gl_Position.x) + (triangle.pointArray[2].c.gl_Position.x - triangle.pointArray[1].c.gl_Position.x) * (y - triangle.pointArray[2].c.gl_Position.y);
  float lambda2Up = (triangle.pointArray[2].c.gl_Position.y - triangle.pointArray[0].c.gl_Position.y) * (x - triangle.pointArray[2].c.gl_Position.x) + (triangle.pointArray[0].c.gl_Position.x - triangle.pointArray[2].c.gl_Position.x) * (y - triangle.pointArray[2].c.gl_Position.y);

  
  float lambdaDown = (triangle.pointArray[1].c.gl_Position.y - triangle.pointArray[2].c.gl_Position.y) * (triangle.pointArray[0].c.gl_Position.x - triangle.pointArray[2].c.gl_Position.x) + (triangle.pointArray[2].c.gl_Position.x - triangle.pointArray[1].c.gl_Position.x)*(triangle.pointArray[0].c.gl_Position.y - triangle.pointArray[2].c.gl_Position.y);
  float lambda1 = lambda1Up / lambdaDown;
  float lambda2 = lambda2Up / lambdaDown;
  float lambda3 = 1.f - lambda1 - lambda2;

  barycentrics[0] = lambda1;
  barycentrics[1] = lambda2;
  barycentrics[2] = lambda3;

  return barycentrics;
}

float GPU::interpolateAttributeFloat(int i ,std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, float attribVertex1, float attribVertex2, float attribVertex3) {

  float fracUpper = 0;
  float fracLower = 0;
  std::array<float, 3> attribArray = { attribVertex1 ,attribVertex2 ,attribVertex3 };
  for (size_t lowerCount = 0; lowerCount < 3; lowerCount++)
  {
    float pom = barycentrics[lowerCount] * triangleList.at(i).pointArray[lowerCount].c.gl_Position.w;
    fracUpper += pom * attribArray[lowerCount];
    fracLower += pom;
  }
  return fracUpper / fracLower;
  
}

glm::vec2 GPU::interpolateAttributeVec2(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec2 attribVertex1, glm::vec2 attribVertex2, glm::vec2 attribVertex3) {

  glm::vec2 fracUpper(0.0f);
  glm::vec2 fracLower(0.0f);
  std::array<glm::vec2, 3> attribArray = { attribVertex1 ,attribVertex2 ,attribVertex3 };

  for (size_t lowerCount = 0; lowerCount < 3; lowerCount++)
  {
    float pom = barycentrics[lowerCount] * triangleList.at(i).pointArray[lowerCount].c.gl_Position.w;
    fracUpper += pom * attribArray[lowerCount];
    fracLower += pom;
  }
  return fracUpper / fracLower;
}

glm::vec3 GPU::interpolateAttributeVec3(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec3 attribVertex1, glm::vec3 attribVertex2, glm::vec3 attribVertex3) {

  glm::vec3 fracUpper(0.0f);
  glm::vec3 fracLower(0.0f);
  std::array<glm::vec3, 3> attribArray = { attribVertex1 ,attribVertex2 ,attribVertex3 };
  for (size_t lowerCount = 0; lowerCount < 3; lowerCount++)
  {
    float pom = barycentrics[lowerCount] * triangleList.at(i).pointArray[lowerCount].c.gl_Position.w;
    fracUpper += pom * attribArray[lowerCount];
    fracLower += pom;
  }
  return fracUpper / fracLower;
}

glm::vec4 GPU::interpolateAttributeVec4(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec4 attribVertex1, glm::vec4 attribVertex2, glm::vec4 attribVertex3) {

  glm::vec4 fracUpper(0.0f);
  float fracLower(0.0f);
  std::array<glm::vec4, 3> attribArray = { attribVertex1 ,attribVertex2 ,attribVertex3 };

  for (size_t lowerCount = 0; lowerCount < 3; lowerCount++)
  {
    float pom = barycentrics[lowerCount] * triangleList.at(i).pointArray[lowerCount].c.gl_Position.w;
    fracUpper += pom * attribArray[lowerCount];
    fracLower += pom;
  }
  return fracUpper / fracLower;
}

bool GPU::perFragment(int x, int y,float newDepth) {
  if (frameBuffer.depthBuffer[y*frameBuffer.width+x] > newDepth)
  {
    return true;
  }
  return false;
}

float GPU::computeEdgeFunc(OutVertex start,OutVertex end, int x, int y) {
  return (((x + 0.5f) - start.gl_Position.x)*(end.gl_Position.y - start.gl_Position.y) - ((y + 0.5f) - start.gl_Position.y)*(end.gl_Position.x - start.gl_Position.x));
}

bool GPU::pinedEdgeFunc(Triangle triangle,int x, int y) {
  OutVertex A = triangle.pointArray[0].c;
  OutVertex B = triangle.pointArray[1].c;
  OutVertex C = triangle.pointArray[2].c;

  float Ea = computeEdgeFunc(A, B, x, y);
  float Eb = computeEdgeFunc(B, C, x, y);
  float Ec = computeEdgeFunc(C, A, x, y);
  if ((Ea >= 0 && Eb >= 0 && Ec >= 0) || (Ea <= 0 && Eb <= 0 && Ec <= 0))
  {
    return true;
  }
  return false;
}

void            GPU::drawTriangles         (uint32_t  nofVertices){
  /// \todo Tato funkce vykreslí trojúhelníky podle daného nastavení.<br>
  /// Vrcholy se budou vybírat podle nastavení z aktivního vertex pulleru (pomocí bindVertexPuller).<br>
  /// Vertex shader a fragment shader se zvolí podle aktivního shader programu (pomocí useProgram).<br>
  /// Parametr "nofVertices" obsahuje počet vrcholů, který by se měl vykreslit (3 pro jeden trojúhelník).<br>

  int vertexPullerInvCount = 0;

  //iteruje pres vsechny trojuhelniky, vzdy po 3 vrcholech
  for (size_t i = 0; i < nofVertices-2; i+=3)
  {
    InVertex inVertex;
    std::vector<OutVertex> outVertexVector;
    Triangle triangle;
    std::vector<Triangle> triangleList;

    //iteruje pres 3 vrcholy, vytahne 3 vrcholy z pameti pomoci vertex pulleru
    for (size_t j = 0; j < 3; j++) 
    {
      //vytahne aktualni 3 vrcholy pomoci vertexPulleru a prozene data skrz vertexShader, outVertex ulozi do vectoru
      OutVertex outVertex;
      inVertex = pullVertex(vertexPullerInvCount);
      vertexPullerInvCount++;

      //kontrolni nastaveni hodnot xyz na INFINITY, pokud nema bod po spusteni vertex shaderu zadnou souradnici zustava hodnota stejna
      outVertex.gl_Position.x = INFINITY;
      outVertex.gl_Position.y = INFINITY;
      outVertex.gl_Position.z = INFINITY;

      activeProgram->vertexShader(outVertex, inVertex, activeProgram->uniformTable);
      outVertexVector.push_back(outVertex);
    }

    //pomocna promenna pocet vrcholu ktere jdou videt
    int seenCount = 0;

    //vytvoreni struktury Trinagle z OutVertexu
    for (int vertex = 0; vertex < 3; vertex++)
    {
      triangle.pointArray[vertex].c = outVertexVector.at(vertex);
      triangle.pointArray[vertex].isSeen = isSeen(triangle.pointArray[vertex]);
      seenCount += triangle.pointArray[vertex].isSeen;
    }

    //jdou videt 2 vrcholy trojuhelnika
    if (seenCount == 2) 
    {
      Triangle newTriangle1 = triangle;
      Triangle newTriangle2 = triangle;

      if (triangle.pointArray[0].isSeen == 0)
      {
        construct2NewTriangles(&newTriangle1.pointArray[0].c, &newTriangle1.pointArray[2].c, &newTriangle2.pointArray[0].c, triangle.pointArray[0].c, triangle.pointArray[1].c, triangle.pointArray[2].c);
      }
      else if (triangle.pointArray[1].isSeen == 0) {
        construct2NewTriangles(&newTriangle1.pointArray[1].c, &newTriangle1.pointArray[2].c, &newTriangle2.pointArray[1].c, triangle.pointArray[1].c, triangle.pointArray[0].c, triangle.pointArray[2].c);
      }
      else if (triangle.pointArray[2].isSeen == 0)
      {
        construct2NewTriangles(&newTriangle1.pointArray[2].c,&newTriangle1.pointArray[0].c,&newTriangle2.pointArray[2].c, triangle.pointArray[2].c, triangle.pointArray[0].c, triangle.pointArray[1].c);
      }
      triangleList.push_back(newTriangle1);
      triangleList.push_back(newTriangle2);
    }

    //lze videt jeden bod trojuhelnika
    else if (seenCount == 1) 
    {
      Triangle newTriangle = triangle;

      if (triangle.pointArray[0].isSeen == 1)
      {
        constructNewTriangle(&newTriangle.pointArray[1].c, &newTriangle.pointArray[2].c, triangle.pointArray[0].c, triangle.pointArray[1].c, triangle.pointArray[2].c);
      }
      else if (triangle.pointArray[1].isSeen == 1)
      {
        constructNewTriangle(&newTriangle.pointArray[0].c, &newTriangle.pointArray[2].c, triangle.pointArray[1].c, triangle.pointArray[0].c, triangle.pointArray[2].c);
      }
      else if (triangle.pointArray[2].isSeen == 1)
      {
        constructNewTriangle(&newTriangle.pointArray[0].c, &newTriangle.pointArray[1].c, triangle.pointArray[2].c, triangle.pointArray[0].c, triangle.pointArray[1].c);
      }

      triangleList.push_back(newTriangle);

    }
    //cely trojuhelnik jde videt
    else if (seenCount == 3)
    {
      triangleList.push_back(triangle);
    }
    //cely trojuhelnik nejde videt => zahodit 

    // v pripade ze aspon jeden bod trojuhelniku lze videt se pokracuej v pipeline
    if (triangleList.size() != 0)
    {
      //iteruje pres vsechny orezane trojuhelniky
      for (size_t i = 0; i < triangleList.size(); i++)
      {
        //perspektivni deleni + view port transformace
        for (size_t vertex = 0; vertex < 3; vertex++)
        {
          //perspektivni adeleni
          triangleList.at(i).pointArray[vertex].c.gl_Position.x /= triangleList.at(i).pointArray[vertex].c.gl_Position.w;
          triangleList.at(i).pointArray[vertex].c.gl_Position.y /= triangleList.at(i).pointArray[vertex].c.gl_Position.w;
          triangleList.at(i).pointArray[vertex].c.gl_Position.z /= triangleList.at(i).pointArray[vertex].c.gl_Position.w;

          //view port transformace
          triangleList.at(i).pointArray[vertex].c.gl_Position.x = (triangleList.at(i).pointArray[vertex].c.gl_Position.x * .5f + .5f) * frameBuffer.width;
          triangleList.at(i).pointArray[vertex].c.gl_Position.y = (triangleList.at(i).pointArray[vertex].c.gl_Position.y * .5f + .5f) * frameBuffer.height;

        }

        //rasterizace

        //pomocne promenne pro iterovani pres ctyruhelnik okoro trojuhelnika
        float minY;
        float maxY;
        float minX;
        float maxX;

        maxY = minY = triangleList.at(i).pointArray[0].c.gl_Position.y;
        maxX = minX = triangleList.at(i).pointArray[0].c.gl_Position.x;

        //nalezeni ctverce ktery se bude prochazet
        for (size_t vertex = 0; vertex < 3; vertex++)
        {
          minY = fminf(minY, triangleList.at(i).pointArray[vertex].c.gl_Position.y);
          maxY = fmaxf(maxY, triangleList.at(i).pointArray[vertex].c.gl_Position.y);
          minX = fminf(minX, triangleList.at(i).pointArray[vertex].c.gl_Position.x);
          maxX = fmaxf(maxX, triangleList.at(i).pointArray[vertex].c.gl_Position.x);
        }
        
        //smycka pres ctyruhelnik
        for (size_t y = minY; y < maxY; y++)
        {
          for (size_t x = minX; x < maxX; x++)
          {         
            //v pripade ze bod lezi uprostred trojuhelniku se pokracuje v rasterizaci
            if (pinedEdgeFunc(triangleList.at(i),x,y))
            {
              //vypocet barycentrickych souradnic
              std::array<float, 3> barycentrics = calculateBarycentric(triangleList.at(i), x + .5f, y + .5f);

              //nastaveni spravnych souradnic pixelu do inFragmentru
              InFragment inFragment;
              inFragment.gl_FragCoord.x = x + .5f;
              inFragment.gl_FragCoord.y = y + .5f;
              //hloubka ulozena v souradnici Z se interpoluje
              inFragment.gl_FragCoord.z = interpolateAttributeFloat(i, triangleList, barycentrics, triangleList.at(i).pointArray[0].c.gl_Position.z, triangleList.at(i).pointArray[1].c.gl_Position.z, triangleList.at(i).pointArray[2].c.gl_Position.z);


              //interpolace zbylych atributu za pomoci barycentrickych souradnic
              for (size_t attribCount = 0; attribCount < maxAttributes; attribCount++)
              {
                AttributeType type = activeProgram->attributes[attribCount];
                switch (type)
                {
                case AttributeType::FLOAT:
                  inFragment.attributes[attribCount].v1 = interpolateAttributeFloat(i, triangleList, barycentrics, triangleList.at(i).pointArray[0].c.attributes[attribCount].v1, triangleList.at(i).pointArray[1].c.attributes[attribCount].v1, triangleList.at(i).pointArray[2].c.attributes[attribCount].v1);
                  break;
                case AttributeType::VEC2:
                  inFragment.attributes[attribCount].v2 = interpolateAttributeVec2(i, triangleList, barycentrics, triangleList.at(i).pointArray[0].c.attributes[attribCount].v2, triangleList.at(i).pointArray[1].c.attributes[attribCount].v2, triangleList.at(i).pointArray[2].c.attributes[attribCount].v2);
                  break;
                case AttributeType::VEC3:
                  inFragment.attributes[attribCount].v3 = interpolateAttributeVec3(i, triangleList, barycentrics, triangleList.at(i).pointArray[0].c.attributes[attribCount].v3, triangleList.at(i).pointArray[1].c.attributes[attribCount].v3, triangleList.at(i).pointArray[2].c.attributes[attribCount].v3);
                  break;
                case AttributeType::VEC4:
                  inFragment.attributes[attribCount].v4 = interpolateAttributeVec4(i, triangleList, barycentrics, triangleList.at(i).pointArray[0].c.attributes[attribCount].v4, triangleList.at(i).pointArray[1].c.attributes[attribCount].v4, triangleList.at(i).pointArray[2].c.attributes[attribCount].v4);
                  break;
                default: //v pripade EMPTY nic se nedeje
                  break;
                }
              }
              OutFragment outFragment;
              //spusteni fragment shaderu
              activeProgram->fragmentShader(outFragment, inFragment, activeProgram->uniformTable);
              if (y*frameBuffer.width + x <= frameBuffer.width*frameBuffer.height * 4 && y*frameBuffer.width + x <= frameBuffer.width*frameBuffer.height)
              {
                //per fragment operace
                if (inFragment.gl_FragCoord.z < frameBuffer.depthBuffer[y*frameBuffer.width + x])
                {
                  //zapsani barvy a hloubky do frame a depth bufferu
                  size_t cord = (x + y * frameBuffer.width) * 4;
                  frameBuffer.colorBuffer[cord] = outFragment.gl_FragColor.r * 255;
                  frameBuffer.colorBuffer[cord + 1] = outFragment.gl_FragColor.g * 255;
                  frameBuffer.colorBuffer[cord + 2] = outFragment.gl_FragColor.b * 255;
                  frameBuffer.colorBuffer[cord + 3] = outFragment.gl_FragColor.a * 255;
                  frameBuffer.depthBuffer[x + y * frameBuffer.width] = inFragment.gl_FragCoord.z;
                }
              }
            }
          }
        }
      }
    }
  }
}

/// @}
