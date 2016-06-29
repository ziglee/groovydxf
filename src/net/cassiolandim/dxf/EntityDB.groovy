package net.cassiolandim.dxf

import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.tools.HandleGenerator

class EntityDB {

    def database = new LinkedHashMap<String, ClassifiedTags>()
    def handles = new HandleGenerator()

    String addTags(ClassifiedTags tags) {
        String handle
        try {
            handle = tags.getHandle()
        } catch (e) {
            handle = handles.next()
        }
        setItem(handle, tags)
        return handle
    }

    void setItem(String handle, ClassifiedTags entity) {
        database.put handle, entity
    }

    ClassifiedTags getItem(String handle) {
        return database.get(handle)
    }

    void eachItem(Closure closure) {
        database.each(closure)
    }
}
