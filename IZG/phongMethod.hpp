/*!
 * @file
 * @brief This file contains phong rendering method
 *
 * @author Tomáš Milet, imilet@fit.vutbr.cz
 */

#pragma once

#include <student/method.hpp>

/// \addtogroup cpu_side Úkoly v cpu části
/// @{

/**
 * @brief This class holds all variables of phong method.
 */
class PhongMethod: public Method{
  public:
    PhongMethod();
    virtual ~PhongMethod();
    virtual void onDraw(glm::mat4 const&proj,glm::mat4 const&view,glm::vec3 const&light,glm::vec3 const&camera) override;


    /// \todo Zde si vytvořte proměnné, které budete potřebovat (id bufferů, programu, ...)
	BufferID vertexBuffer;
	BufferID indexBuffer;
	VertexPullerID vertexPuller;
	ProgramID program;

	//v pripade ze barva pretekla nastavi jeji hodnotu na 1, v pripade ze podtekla nastavi na 0 jinak barvu necha byt
 	void checkUnderAndOverFlow(float* color);
};

/// @}
