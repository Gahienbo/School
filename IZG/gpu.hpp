/*!
 * @file
 * @brief This file contains class that represents graphic card.
 *
 * @author Tomáš Milet, imilet@fit.vutbr.cz
 */
#pragma once

#include <student/fwd.hpp>
#include<vector>
#include <map>
#include<unordered_map>

#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <math.h>
#include <array>


/**
 * @brief This class represent software GPU
 */
class GPU{
  public:
    GPU();
    virtual ~GPU();

    //buffer object commands
    BufferID  createBuffer           (uint64_t size);
    void      deleteBuffer           (BufferID buffer);
    void      setBufferData          (BufferID buffer,uint64_t offset,uint64_t size,void const* data);
    void      getBufferData          (BufferID buffer,uint64_t offset,uint64_t size,void      * data);
    bool      isBuffer               (BufferID buffer);

    //vertex array object commands (vertex puller)
    ObjectID  createVertexPuller     ();
    void      deleteVertexPuller     (VertexPullerID vao);
    void      setVertexPullerHead    (VertexPullerID vao,uint32_t head,AttributeType type,uint64_t stride,uint64_t offset,BufferID buffer);
    void      setVertexPullerIndexing(VertexPullerID vao,IndexType type,BufferID buffer);
    void      enableVertexPullerHead (VertexPullerID vao,uint32_t head);
    void      disableVertexPullerHead(VertexPullerID vao,uint32_t head);
    void      bindVertexPuller       (VertexPullerID vao);
    void      unbindVertexPuller     ();
    bool      isVertexPuller         (VertexPullerID vao);

    //program object commands
    ProgramID createProgram          ();
    void      deleteProgram          (ProgramID prg);
    void      attachShaders          (ProgramID prg,VertexShader vs,FragmentShader fs);
    void      setVS2FSType           (ProgramID prg,uint32_t attrib,AttributeType type);
    void      useProgram             (ProgramID prg);
    bool      isProgram              (ProgramID prg);
    void      programUniform1f       (ProgramID prg,uint32_t uniformId,float     const&d);
    void      programUniform2f       (ProgramID prg,uint32_t uniformId,glm::vec2 const&d);
    void      programUniform3f       (ProgramID prg,uint32_t uniformId,glm::vec3 const&d);
    void      programUniform4f       (ProgramID prg,uint32_t uniformId,glm::vec4 const&d);
    void      programUniformMatrix4f (ProgramID prg,uint32_t uniformId,glm::mat4 const&d);

    //framebuffer functions
    void      createFramebuffer      (uint32_t width,uint32_t height);
    void      deleteFramebuffer      ();
    void      resizeFramebuffer      (uint32_t width,uint32_t height);
    uint8_t*  getFramebufferColor    ();
    float*    getFramebufferDepth    ();
    uint32_t  getFramebufferWidth    ();
    uint32_t  getFramebufferHeight   ();

    //execution commands
    void      clear                  (float r,float g,float b,float a);
    void      drawTriangles          (uint32_t  nofVertices);

    /// \addtogroup gpu_init 00. proměnné, inicializace / deinicializace grafické karty
    /// @{
    /// \todo zde si můžete vytvořit proměnné grafické karty (buffery, programy, ...)
    /// @}

    //vertex puller
    struct vertexHead
    {
        size_t offset;
        uint64_t stride;
        AttributeType size;
        BufferID bufferID;
        bool enabled;
    };

    struct IndexBuffer {
        BufferID id;
        IndexType indexType;
        size_t offset;
    };

    struct vertexTable
    {
        std::array<vertexHead, maxAttributes> vertexHeadArray;
        bool indexEnable = false;
        IndexBuffer indexBuffer;
    };

    //program
    struct Program {
        VertexShader vertexShader;
        FragmentShader fragmentShader;
        Uniforms uniformTable;
        std::array<AttributeType, maxAttributes> attributes;
    };

    //framebuffer
    struct FrameBuffer {
        uint32_t width;
        uint32_t height;
        uint8_t* colorBuffer = NULL;
        float* depthBuffer = NULL;
    };

    //buffery
    std::map<BufferID, std::vector<uint8_t>> bufferMap; //seznam bufferu
    BufferID bufferCount = 0;

    //vertex pullery
    std::map<VertexPullerID, vertexTable> vertexPullers;
    VertexPullerID vertexCount = 0;
    vertexTable* activeVertex = nullptr;

    //programy
    std::map<ProgramID, Program> programs;
    ProgramID programCount = 0;
    Program* activeProgram = nullptr;

    //frame buffer
    FrameBuffer* frameBufferPointer = {};
    FrameBuffer frameBuffer;

    //draw triangles
    uint32_t inVertexCount = 0;

    struct TrianglePoint {
        OutVertex c;
        int isSeen;
    };

    struct Triangle {
        std::array<TrianglePoint, 3> pointArray;
    };

    int isSeen(TrianglePoint point);
    OutVertex calculateCutPoint(OutVertex A, OutVertex B);

    struct TriangleLine {
        float a;
        float b;
        float c;

        float k;
        float q;

        OutVertex start;
        OutVertex end;
    };

    //draw triangles pomocne fce
    InVertex pullVertex(int invocationCount);
    void construct2NewTriangles(OutVertex* newTriangle1Point1, OutVertex* newTriangle1Point2, OutVertex* newTriangle2Point1, OutVertex notSeen, OutVertex b, OutVertex c);
    void constructNewTriangle(OutVertex* newTriangle1Point1, OutVertex* newTriangle1Point2, OutVertex notSeen, OutVertex b, OutVertex c);
    std::array<float, 3> calculateBarycentric(Triangle triangle, float x, float y);
    float interpolateAttributeFloat(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, float attribVertex1, float atribVertex2, float atribVertex3);
    glm::vec2 interpolateAttributeVec2(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec2 attribVertex1, glm::vec2 atribVertex2, glm::vec2 atribVertex3);
    glm::vec3 interpolateAttributeVec3(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec3 attribVertex1, glm::vec3 atribVertex2, glm::vec3 atribVertex3);
    glm::vec4 interpolateAttributeVec4(int i, std::vector<Triangle> triangleList, std::array<float, 3> barycentrics, glm::vec4 attribVertex1, glm::vec4 atribVertex2, glm::vec4 atribVertex3);
    bool perFragment(int x,int y,float newDepth);
    bool pinedEdgeFunc(Triangle triangle, int x, int y);
    float computeEdgeFunc(OutVertex start, OutVertex end, int x, int y);
};


