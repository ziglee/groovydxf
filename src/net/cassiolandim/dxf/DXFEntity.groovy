package net.cassiolandim.dxf

import net.cassiolandim.dxf.graphics.Polyline
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr3
import net.cassiolandim.dxf.lldxf.DXFAttributes
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.lldxf.Tags
import net.cassiolandim.dxf.lldxf.Types

abstract class DXFEntity {

    ClassifiedTags tags
    DXFNamespace dxf
    Drawing drawing

    DXFEntity(ClassifiedTags tags, Drawing drawing = null) {
        this.tags = tags
        this.dxf = new DXFNamespace(this) // all DXF attributes are accessible by the dxf attribute, like entity.dxf.handle
        this.drawing = drawing
    }

    DXFEntity(String handle, Map dxfAttribs = [:], Drawing drawing = null) {
        def TEMPLATE = getTemplate()
        if (TEMPLATE == null) {
            throw new UnsupportedOperationException("new() for type ${this.class} not implemented.")
        }

        this.tags = TEMPLATE
        this.dxf = new DXFNamespace(this) // all DXF attributes are accessible by the dxf attribute, like entity.dxf.handle
        this.drawing = drawing

        dxf.handle = handle
        if (dxfAttribs)
            updateDxfAttribs(dxfAttribs)
        postNewHook()
    }

    abstract ClassifiedTags getTemplate();
    abstract DXFAttributes getDefaulDxfAttribs();

    def getDxfAttrib = { String key, def defaultValue = null ->
        def DXFATTRIBS = getDefaulDxfAttribs()
        DXFAttr3 dxfAttr = DXFATTRIBS[key]
        if (!dxfAttr) throw new NoSuchElementException("DXFAttrib '${key}' does not exist.")

        try {
            return getDxfAttrib(dxfAttr)
        } catch (NoSuchElementException nsee) {
            if (defaultValue == null) {
                // no DXF default values if DXF version is incorrect
                if (dxfAttr.dxfVersion && drawing && drawing.dxfVersion < dxfAttr.dxfVersion) {
                    throw new NoSuchElementException("DXFAttrib '${key}' not supported by DXF version '${drawing.dxfVersion}', requires at least DXF version '${dxfAttr.dxfVersion}'.")
                }
                def result = dxfAttr.defauult // default value defined by DXF specs
                if (result != null) {
                    return result
                } else {
                    throw new NoSuchElementException("DXFAttrib '${key}' does not exist.")
                }
            }
            return defaultValue
        }
    }

    def setDxfAttrib = { String key, def value ->
        def DXFATTRIBS = getDefaulDxfAttribs()
        DXFAttr3 dxfAttr = DXFATTRIBS[key]
        if (dxfAttr == null) throw new NoSuchElementException("DXFAttrib '${key}' does not exist.")
        if (dxfAttr.dxfVersion && drawing) {
            if (drawing.dxfVersion < dxfAttr.dxfVersion) {
                throw new NoSuchElementException("DXFAttrib '${key}' not supported by DXF version '${drawing.dxfVersion}', requires at least DXF version '${dxfAttr.dxfVersion}'.")
            }
        }
        // no subclass is subclass index 0
        Tags subClassTags = tags.subClasses.getAt(dxfAttr.subClass)
        if (dxfAttr.xType != null) {
            setExtendedType(subClassTags, dxfAttr.code, dxfAttr.xType, value)
        } else {
            subClassTags.setFirst(dxfAttr.code, Types.castTagValue(dxfAttr.code, value))
        }

        if (this instanceof Polyline) {
            if (key.equals('layer')) // if layer of POLYLINE changed, also change layer of VERTEX entities
                ((Polyline)this).setVerticesLayer(value)
        }
    }

    def delDxfAttrib = { String key ->
        def DXFATTRIBS = getDefaulDxfAttribs()
        DXFAttr3 dxfAttr = DXFATTRIBS[key]
        if (dxfAttr == null) throw new NoSuchElementException("DXFAttrib '${key}' does not exist.")
        delDxfAttrib(dxfAttr)
    }

    def getDxfAttrib(DXFAttr3 dxfAttr) {
        // no subclass is subclass index 0
        Tags subClassTags = tags.subClasses.getAt(dxfAttr.subClass)
        if (dxfAttr.xType != null)
            return getExtentedType(subClassTags, dxfAttr.code, dxfAttr.xType)
        else
            return subClassTags.findFirst(dxfAttr.code)
    }

    def delDxfAttrib(DXFAttr3 dxfAttr) {
        def pointCodes = { int baseCode ->
            return [baseCode, baseCode + 10, baseCode + 20]
        }

        Tags subClassTags = tags.subClasses.get(dxfAttr.subClass)
        if (dxfAttr.xType != null)
            subClassTags.removeTags(pointCodes(dxfAttr.code))
        else
            subClassTags.removeTags([dxfAttr.code])
    }

    private static def getExtentedType(Tags subClassTags, int code, String xType) {
        def value = subClassTags.findFirst(code)
        if (value instanceof List) {
            if (value.size() == 3) {
                if (xType.equals('Point2D'))
                    throw new Tagger.DXFStructureError("expected 2D point but found 3D point")
            } else if (xType.equals('Point3D')) { // value.size() == 2
                throw new Tagger.DXFStructureError("expected 3D point but found 2D point")
            }
        }
        return value
    }

    static def setExtendedType(Tags tags, int code, String xType, def value) {
        value = Types.castTagValue(code, value)
        def vLen = value.size()
        if (vLen == 3) {
            if (xType.equals('Point2D')) throw new IllegalStateException('2 axis required')
        } else if (vLen == 2) {
            if (xType.equals('Point3D')) throw new IllegalStateException('3 axis required')
        } else {
            throw new IllegalStateException('2 or 3 axis required')
        }
        tags.setFirst(code, value)
    }

    void updateDxfAttribs(Map dxfAttribs) {
        dxfAttribs.each { String key, def value ->
            setDxfAttrib(key, value)
        }
    }

    /**
     * Called after entity creation.
     */
    void postNewHook() {
        // pass
    }

    /**
     * Create new entity with same layout settings as *self*.
     * Used by INSERT & POLYLINE to create appended DXF entities, don't use it to create new standalone entities.
     */
    protected DXFEntity newEntity(String type, Map dxfAttribs) {
        def entity = drawing.dxfFactory.createDbEntry(type, dxfAttribs)
        drawing.dxfFactory.copyLayout(this, entity)
        return entity
    }

    /**
     * Provides the dxf namespace for GenericWrapper.
     */
    private class DXFNamespace {

        Closure getter
        Closure setter
        Closure deletter

        DXFNamespace(DXFEntity wrapper) {
            getter = wrapper.getDxfAttrib
            setter = wrapper.setDxfAttrib
            deletter = wrapper.delDxfAttrib
        }

        @Override
        void setProperty(String property, Object newValue) {
            setter(property, newValue)
        }

        @Override
        Object getProperty(String property) {
            return getter(property)
        }

        void delProperty(String property) {
            deletter(property)
        }
    }
}
