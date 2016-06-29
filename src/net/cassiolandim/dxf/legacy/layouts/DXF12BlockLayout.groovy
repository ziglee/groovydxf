package net.cassiolandim.dxf.legacy.layouts

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.EntityDB
import net.cassiolandim.dxf.EntitySpace
import net.cassiolandim.dxf.legacy.LegacyDXFFactory

/**
 * BlockLayout has the same factory-function as Layout, but is managed
 * in the BlocksSection() class. It represents a DXF Block definition.
 */
class DXF12BlockLayout extends BaseLayout {

    String blockHandle // db handle to BLOCK entity
    String endBlkHandle // db handle to ENDBLK entity
    // entitySpace is the block content

    DXF12BlockLayout(EntityDB entityDB, LegacyDXFFactory factory, String blockHandle, String endBlkHandle) {
        super(factory, new EntitySpace(entityDB))
        this.blockHandle = blockHandle
        this.endBlkHandle = endBlkHandle
    }

    /**
     * Add entity by handle to the block entity space.
     */
    def addHandle(handle) {
        entitySpace.add(handle)
    }

    def getBlock() {
        return getEntityByHandle(blockHandle)
    }

    String getName() {
        return block.dxf.name
    }

    void write(File stream) {
        def writeTags = { handle ->
            def tags = entitySpace.getTagsByHandle(handle)
            tags.write(stream)
        }

        writeTags(blockHandle)
        entitySpace.write(stream)
        writeTags(endBlkHandle)
    }

    void addEntity(DXFEntity entity) {
        addHandle(entity.dxf.handle)
    }
}
