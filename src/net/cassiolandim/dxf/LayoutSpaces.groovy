package net.cassiolandim.dxf

import net.cassiolandim.dxf.lldxf.ClassifiedTags

class LayoutSpaces {

    Map<Integer, EntitySpace> layoutSpaces = [:]
    EntityDB entityDB

    LayoutSpaces(EntityDB entityDB) {
        this.entityDB = entityDB
    }

    def getKey(ClassifiedTags tags) {
        return tags.noClass.findFirst(67, 0) // paper space value
    }

    /**
     * Get entity space by *key* or create new entity space.
     */
    EntitySpace getEntitySpace(int key) {
        def entitySpace = layoutSpaces.get(key)
        if (!entitySpace) { // create new entity space
            entitySpace = new EntitySpace(entityDB)
            setEntitySpace(key, entitySpace)
        }
        return entitySpace
    }

    void setEntitySpace(int key, EntitySpace entitySpace) {
        layoutSpaces.put key, entitySpace
    }

    /**
     * Store *tags* in associated layout entity space.
     */
    void storeTags(ClassifiedTags tags) {
        def entitySpace = getEntitySpace(getKey(tags))
        entitySpace.storeTags(tags)
    }

    /**
     * Write all entity spaces to *stream*.
     * If *keys* is not *None*, write only entity spaces defined in *keys*.
     */
    void write(File stream, def keys = null) {
        if (!keys)
            keys = layoutSpaces.keySet()
        for (key in keys)
            layoutSpaces.get(key).write(stream)
    }
}
