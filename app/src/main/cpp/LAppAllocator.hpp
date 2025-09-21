/**
 * Copyright(c) Live2D Inc. All rights reserved.
 */

#pragma once

#include "ICubismAllocator.hpp"

namespace Live2D { namespace Cubism { namespace Framework {

/**
 * @brief Example allocator class implementing ICubismAllocator
 */
            class LAppAllocator : public ICubismAllocator
            {
            public:
                LAppAllocator();
                virtual ~LAppAllocator();

                void* Allocate(const csmSizeType size) override;
                void Deallocate(void* memory) override;

                void* AllocateAligned(const csmSizeType size, const csmUint32 alignment) override;
                void DeallocateAligned(void* alignedMemory) override;
            };

        }}}
