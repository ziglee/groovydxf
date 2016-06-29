package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Polyline extends GraphicEntity {

    private static final int POLYLINE_CLOSED = 1
    private static final int POLYLINE_MESH_CLOSED_M_DIRECTION = POLYLINE_CLOSED
    private static final int POLYLINE_CURVE_FIT_VERTICES_ADDED = 2
    private static final int POLYLINE_SPLINE_FIT_VERTICES_ADDED = 4
    private static final int POLYLINE_3D_POLYLINE = 8
    private static final int POLYLINE_3D_POLYMESH = 16
    private static final int POLYLINE_MESH_CLOSED_N_DIRECTION = 32
    private static final int POLYLINE_POLYFACE = 64
    private static final int POLYLINE_GENERATE_LINETYPE_PATTERN = 128

    private static final String POLYLINE_TPL = """  0
POLYLINE
  5
0
  8
0
 66
1
 70
0
 10
0.0
 20
0.0
 30
0.0
"""

    Polyline(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Polyline(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    GraphicEntity cast() {
        String mode = getMode()
        if (mode.equals('AcDbPolyFaceMesh')) {
            return Polyface.convert(this)
        } else if (mode.equals('AcDbPolygonMesh')) {
            return Polymesh.convert(this)
        }
        return this
    }

    @Override
    void postNewHook() {
        def seqEnd = newEntity('SEQEND', [:])
        tags.link = seqEnd.dxf.handle
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(POLYLINE_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'elevation': new DXFAttr(10, 'Point2D/3D'),
                'flags': new DXFAttr(70, null, 0),
                'default_start_width': new DXFAttr(40, null, 0.0),
                'default_end_width': new DXFAttr(41, null, 0.0),
                'm_count': new DXFAttr(71, null, 0),
                'n_count': new DXFAttr(72, null, 0),
                'm_smooth_density': new DXFAttr(73, null, 0),
                'n_smooth_density': new DXFAttr(74, null, 0),
                'smooth_type': new DXFAttr(75, null, 0)
        ])
    }

    void setVerticesLayer(String layerName) {
        for (vertex in vertices()) {
            vertex.dxf.layer = layerName
        }
    }

    def vertices() {
        def list = new ArrayList()
        def wrapper = drawing.dxfFactory.wrapHandle
        def handle = tags.link
        while (handle != null) {
            def entity = wrapper(handle)
            handle = entity.tags.link
            if (entity.dxftype().equals('VERTEX')) {
                list.add(entity)
            }
        }
        return list
    }

    def getMode() {
        if (is3dPolyline)
            return 'AcDb3dPolyline'
        else if (isPolygonMesh)
            return 'AcDbPolygonMesh'
        else if (isPolyFaceMesh)
            return 'AcDbPolyFaceMesh'
        else
            return 'AcDb2dPolyline'
    }

    def getIs3dPolyline() {
        return dxf.flags & POLYLINE_3D_POLYLINE
    }

    def getIsPolygonMesh() {
        return dxf.flags & POLYLINE_3D_POLYMESH
    }

    def getIsPolyFaceMesh() {
        return dxf.flags & POLYLINE_POLYFACE
    }

    def getVertexFlags() {
        return Vertex.VERTEX_FLAGS.get(getMode())
    }

    void appendVertices(def points, Map dxfAttribs = [:]) {
        if (points && !points.isEmpty()) {
            def lastVertex = getLastVertex()
            for (newVertex in pointsToDxfVertices(points, dxfAttribs)) {
                insertAfter(lastVertex, newVertex)
                lastVertex = newVertex
            }
        }
    }

    static void insertAfter(DXFEntity prevVertex, DXFEntity newVertex) {
        def succ = prevVertex.tags.link
        prevVertex.tags.link = newVertex.dxf.handle
        newVertex.tags.link = succ
    }

    DXFEntity getLastVertex() {
        ClassifiedTags tags = getTags()
        String handle = dxf.handle
        String prevHandle = null
        while (tags.link != null) { // while not SEQEND
            prevHandle = handle
            handle = tags.link
            tags = drawing.entitydb.getItem(handle)
        }
        return drawing.dxfFactory.wrapHandle(prevHandle)
    }

    /**
     * Converts point (x,y, z)-tuples into DXF Vertex() objects.
     * @param points list of (x, y,z)-tuples
     * @param dxfAttribs dict of DXF attributes
     */
    List<DXFEntity> pointsToDxfVertices(def points, Map dxfAttribs) {
        dxfAttribs.put('flags', dxfAttribs.get('flags', 0) | getVertexFlags())
        dxfAttribs.put('layer', getDxfAttrib('layer', '0')) // all vertices on the same layer as the POLYLINE entity
        def vertices = new ArrayList<DXFEntity>()
        for (point in points) {
            dxfAttribs.put('location', point)
            vertices.add(newEntity('VERTEX', dxfAttribs))
        }
        return vertices
    }

    def close(boolean mCloseP, boolean nCloseP = false) {
        if (mCloseP) mClose()
        if (nCloseP) nClose()
    }

    def mClose() {
        dxf.flags = dxf.flags | POLYLINE_MESH_CLOSED_M_DIRECTION
    }

    def nClose() {
        dxf.flags = dxf.flags | POLYLINE_MESH_CLOSED_N_DIRECTION
    }

    def getAt(int pos) {
        def count = 0
        def db = drawing.entitydb
        def tags = db.getItem(tags.link)
        while (tags.link != null) {
            if (count == pos)
                return drawing.dxfFactory.wrapEntity(tags)
            count++
            tags = db.getItem(tags.link)
        }
        throw new IndexOutOfBoundsException("vertex index #${pos} out of range")
    }
}
