package net.cassiolandim.dxf.legacy.layouts

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.EntitySpace
import net.cassiolandim.dxf.GraphicsFactory
import net.cassiolandim.dxf.graphics.GraphicEntity
import net.cassiolandim.dxf.legacy.LegacyDXFFactory

/**
 * Base class for DXF12Layout() and DXF12BlockLayout()
 * Entities are wrapped into class GraphicEntity() or inherited.
 */
class BaseLayout extends GraphicsFactory {

    EntitySpace entitySpace

    BaseLayout(LegacyDXFFactory factory, EntitySpace entitySpace) {
        super(factory)
        this.entitySpace = entitySpace
    }

    /**
     * Get entity by handle as GraphicEntity() or inherited.
     */
    DXFEntity getEntityByHandle(String handle) {
        return dxfFactory.wrapHandle(handle)
    }

    /**
     * Create entity in drawing database and add entity to the entity space.
     * @param type DXF type string, like 'LINE', 'CIRCLE' or 'LWPOLYLINE'
     * @param dxfAttribs DXF attributes for the new entity
     * build_and_add_entity
     */
    @Override
    def buildAndAddEntity(String type, Map dxfAttribs) {
        def entity = buildEntity(type, dxfAttribs)
        addEntity(entity)
        return entity
    }

    /**
     * Create entity in drawing database, returns a wrapper class inherited from GraphicEntity().
     * Adds entity to the drawing database.
     */
    def buildEntity(String type, Map dxfAttribs) {
        def entity = dxfFactory.createDbEntry(type, dxfAttribs)
        setPaperSpace(entity)
        return entity
    }

    /**
     * Add entity to entity space but not to the drawing database.
     */
    void addEntity(DXFEntity entity) {
        entitySpace.add(entity.dxf.handle)
        setPaperSpace(entity)
    }

    void setPaperSpace(DXFEntity entity) {}
}
