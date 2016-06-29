package net.cassiolandim.dxf.legacy

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.graphics.*
import net.cassiolandim.dxf.tableEntries.*
import net.cassiolandim.dxf.legacy.layouts.DXF12BlockLayout
import net.cassiolandim.dxf.legacy.layouts.DXF12Layouts

import java.lang.reflect.Method

class LegacyDXFFactory {

    Drawing drawing
    private static final DEFAULT_WRAPPER = GraphicEntity

    private static final Map<String, Class<? extends DXFEntity>> ENTITY_WRAPPERS = [
        // tables entries
        'LAYER': Layer,
        'DIMSTYLE': DimStyle,
        'LTYPE': Linetype,
        'APPID': AppID,
        'STYLE': Style,
        'UCS': UCS,
        'VIEW': View,
        'VPORT': Viewport,
        // dxf entities
        'LINE': Line,
        'POINT': Point,
        'CIRCLE': Circle,
        'ARC': Arc,
        'TRACE': Trace,
        'SOLID': Solid,
        '3DFACE': Face,
        'TEXT': Text,
        'ATTRIB': Attrib,
        'ATTDEF': Attdef,
        'INSERT': Insert,
        'BLOCK': Block,
        'ENDBLK': EndBlk,
        'POLYLINE': Polyline,
        'VERTEX': Vertex,
        'SEQEND': SeqEnd,
        'SHAPE': Shape,
        'VIEWPORT': Viewport
    ]

    public LegacyDXFFactory(Drawing drawing) {
        this.drawing = drawing
    }

    def headerVarFactory = { def key, def value ->
        def factory = HeaderVars.VARMAP.get(key)
        return factory(value)
    }

    DXF12BlockLayout newBlockLayout(String blockHandle, String endBlkHandle) {
        return new DXF12BlockLayout(drawing.entitydb, this, blockHandle, endBlkHandle)
    }

    DXFEntity wrapHandle(String handle) {
        def tags = drawing.entitydb.getItem(handle)
        return wrapEntity(tags)
    }

    DXFEntity wrapEntity(def tags) {
        def wrapper = ENTITY_WRAPPERS.get(tags.dxfType()) ?: DEFAULT_WRAPPER
        def entity = wrapper.newInstance([tags, drawing] as Object[])
        if (entity instanceof Polyline) {
            entity = ((Polyline) entity).cast()
        }
        return entity
    }

    DXF12Layouts getLayouts() {
        return new DXF12Layouts(drawing)
    }

    /**
     * Create new entity and add to drawing-database.
     */
    DXFEntity createDbEntry(String type, Map dxfAttribs) {
        def handle = drawing.entitydb.handles.next()
        def dbEntry = newEntity(type, handle, dxfAttribs)
        drawing.entitydb.setItem(handle, dbEntry.tags)
        return dbEntry
    }

    /**
     * Create a new entity.
     */
    DXFEntity newEntity(String type, String handle, Map dxfAttribs) {
        def wrapper = ENTITY_WRAPPERS.get(type)
        if (!wrapper) throw new NoSuchElementException("Unsupported entity type: ${type}")
        return wrapper.newInstance([handle, dxfAttribs, drawing] as Object[])
    }

    /**
     * Place target_entity in same layout as source_entity
     */
    void copyLayout(DXFEntity sourceEntity, DXFEntity targetEntity) {
        targetEntity.dxf.paperspace = sourceEntity.dxf.paperspace
    }
}
