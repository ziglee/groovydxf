package net.cassiolandim.dxf.lldxf

/**
 * Group of tags starts with a SplitTag and ends before the next SplitTag. A SplitTag is a tag with
 * code == splitcode, like (0, 'SECTION') for splitcode == 0.
 */
class TagGroups extends ArrayList<List<DXFTag>> {

    int splitCode

    TagGroups(List<DXFTag> tags, int splitCode = 0) {
        this.splitCode = splitCode

        Tags group = null
        def append = { ctags -> }

        for (DXFTag tag in tags) {
            if (tag.code == splitCode) {
                if (group != null) {
                    add(group)
                }
                group = new Tags([tag])
                append = { ctags -> group.add(ctags) }
            } else {
                append(tag)
            }
        }
        if (group != null) {
            add(group)
        }
    }

    String getName(int index) {
        return this[index][0].value
    }
}
