package net.cassiolandim.dxf.lldxf

import net.cassiolandim.dxf.Drawing

class Repair {

    /**
     * Enable 'handles' for DXF R12 to be consistent with later DXF versions.
     * Write entitydb-handles into entity-tags.
     */
    static void enableHandles(Drawing dwg) {
        def hasHandle = { ClassifiedTags tags, int handleCode ->
            for (tag in tags.noClass) {
                if (tag.code == handleCode)
                    return true
            }
            return false
        }
        def putHandlesIntoEntityTags = {
            dwg.entitydb.eachItem { String handle, ClassifiedTags tags ->
                DXFTag noClass = tags.noClass[0]
                def isNotDimStyle = !noClass.equals(new DXFTag(0, 'DIMSTYLE'))
                int handleCode = isNotDimStyle ? 5 : 105 // legacy shit!!!
                if (!hasHandle(tags, handleCode)) {
                    tags.noClass.add(1, new DXFTag(handleCode, handle)) // handle should be the 2. tag
                }
            }
        }

        putHandlesIntoEntityTags()
        dwg.sections.header.set('$HANDLING', 1)
    }

    static void putHandlesIntoEntityTags() {

    }
}
