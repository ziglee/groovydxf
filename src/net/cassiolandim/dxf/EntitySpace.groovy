package net.cassiolandim.dxf

import net.cassiolandim.dxf.lldxf.ClassifiedTags

/**
 * An EntitySpace is a collection of drawing entities.
 * The ENTITY section is such an entity space, but also blocks.
 * The EntitySpace stores only handles to the drawing entity database.
 */
class EntitySpace extends ArrayList<String> {

    EntityDB entitydb

    EntitySpace(EntityDB entityDB) {
        this.entitydb = entityDB
    }

    def storeTags(ClassifiedTags tags) {
        def handle
        try {
            handle = tags.handle
        } catch (e) { // no handle tag available
            // handle is not stored in tags!!!
            handle = entitydb.handles.next()
        }
        add(handle)
        entitydb.setItem(handle, tags)
        return handle
    }

    def getTagsByHandle(String handle) {
        return entitydb.getItem(handle)
    }

    void write(File stream) {
        for (String handle in this) {
            // write linked entities
            while (handle) {
                def tags = entitydb.getItem(handle)
                tags.write(stream)
                handle = tags.link
            }
        }
    }
}