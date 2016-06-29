package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.DefaultChunk
import net.cassiolandim.dxf.lldxf.TagGroups
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.lldxf.Tags

class TablesSection {

    private static def TABLESMAP = [
        'LAYER': Table,
        'LTYPE': Table,
        'STYLE': Table,
        'DIMSTYLE': Table,
        'VPORT': ViewportTable,
        'VIEW': Table,
        'UCS': Table,
        'APPID': Table,
        'BLOCK_RECORD': Table,
    ]

    private static def TABLENAMES = [
        'layer': 'layers',
        'ltype': 'linetypes',
        'appid': 'appids',
        'dimstyle': 'dimstyles',
        'style': 'styles',
        'ucs': 'ucs',
        'view': 'views',
        'vport': 'viewports',
        'block_record': 'block_records'
    ]

    // The order of the tables may change, but the LTYPE table always precedes the LAYER table.
    private static def TABLE_ORDER = ['viewports', 'linetypes', 'layers', 'styles', 'views', 'ucs', 'appids', 'dimstyles', 'block_records']

    private static String MIN_TABLE_SECTION = """  0
SECTION
  2
TABLES
  0
ENDSEC
"""

    private static String MIN_TABLE = """  0
TABLE
  2
DUMMY
 70
0
  0
ENDTAB
"""

    Drawing drawing
    def tables = [:]

    TablesSection(Tags tags, Drawing drawing) {
        this.drawing = drawing
        if (!tags) {
            tags = Tags.fromText(MIN_TABLE_SECTION)
        }
        setupTables(tags)
    }

    String getName() {
        return 'tables'
    }

    def setupTables(Tags tags) {
        if (!tags[0].equals(new DXFTag(0, 'SECTION')) || !tags[1].equals(new DXFTag(2, 'TABLES')) || !tags[-1].equals(new DXFTag(0, 'ENDSEC'))) {
            throw new Tagger.DXFStructureError("Critical structure error in TABLES section.")
        }

        def skipTags = { def iterator, int count ->
            (1..count).each {
                iterator.next()
            }
            return iterator
        }

        def tagsIterator = skipTags(tags.iterator(), 2) // (0, 'SECTION'), (2, 'TABLES')
        for (Tags tableTags in DefaultChunk.iterChunks(tagsIterator, 'ENDSEC', 'ENDTAB')) {
            newTable(tableTags[1].value, tableTags)
        }
    }

    void newTable(String name, Tags tags) {
        def tableClass = TABLESMAP.get(name) ?: GenericTable
        def newTable = tableClass.newInstance(tags, drawing)
        tables.put(newTable.name, newTable)
    }

    /**
     * Translate DXF-table-name to attribute-name. ('LAYER' -> 'layers')
     */
    static String tableName(String dxfName) {
        def name = dxfName.toLowerCase()
        return TABLENAMES.get(name) ?: name + 's'
    }

    public static class GenericTable extends DefaultChunk {

        GenericTable(Tags tags, Drawing drawing) {
            super(tags, drawing)
        }

        String getName() {
            return tableName(tags[1].value)
        }
    }

    void write(File stream) {
        stream.append('  0\nSECTION\n  2\nTABLES\n')
        for (String tableName in TABLE_ORDER) {
            def table = tables.get(tableName)
            if (table)
                table.write(stream)
        }
        stream.append('  0\nENDSEC\n')
    }

    public static class Table {

        Drawing drawing
        String dxfName
        def tableEntries = []
        ClassifiedTags tableHeader

        Table(Tags tags, Drawing drawing) {
            this.drawing = drawing
            this.dxfName = tags[1].value.toString()
            buildTableEntries(tags)
        }

        String getName() {
            return tableName(dxfName)
        }

        void buildTableEntries(Tags tags) {
            def groups = new TagGroups(tags)
            if (!groups.getName(0).equals('TABLE') || !groups.getName(-1).equals('ENDTAB'))
                throw new Tagger.DXFStructureError("Critical structure error in TABLES section.")

            this.tableHeader = new ClassifiedTags(groups[0].subList(1, groups[0].size()))

            /*
             * AC1009 table headers have no handles, but putting it into the entitydb, will give it a handle and corrupt
             # the DXF format.
             # if self._drawing.dxfversion != 'AC1009':
             */
            drawing.entitydb.addTags(this.tableHeader)
            for (List<DXFTag> ctags in groups.subList(1, groups.size() - 1)) {
                addEntry(new ClassifiedTags(ctags))
            }
        }

        /**
         * Add table-entry to table and entitydb.
         */
        void addEntry(def entry) {
            def handle
            def tags
            try {
                try {
                    handle = entry.handle
                } catch (NumberFormatException nfe) {
                    handle = drawing.entitydb.handles.next()
                }
                tags = entry
            } catch (MissingMethodException e) {
                handle = entry.dxf.handle
                tags = entry.tags
            }
            drawing.entitydb.setItem(handle, tags)
            appendEntryHandle(handle)
        }

        void appendEntryHandle(String handle) {
            if (!tableEntries.contains(handle)) {
                tableEntries.add handle
            }
        }

        /**
         * Write DXF representation to stream, stream opened with mode='wt'.
         */
        def write(File stream) {
            def prologue = {
                stream.append('  0\nTABLE\n')
                updateMetaData()
                tableHeader.write(stream)
            }

            def content = {
                for (ClassifiedTags tags in iterTableEntriesAsTags())
                    tags.write(stream)
            }

            def epilogue = {
                stream.append('  0\nENDTAB\n')
            }

            prologue()
            content()
            epilogue()
        }

        def updateMetaData() {
            def count = tableEntries.size()
            tableHeader.noClass.update(70, count)
        }

        /**
         * Iterate over table-entries as Tags().
         */
        def iterTableEntriesAsTags() {
            return tableEntries.collect { handle ->
                drawing.entitydb.getItem(handle)
            }
        }
    }

    /**
     * Viewport-Table can have multiple entries with same name
     */
    public static class ViewportTable extends Table {

        ViewportTable(Tags tags, Drawing drawing) {
            super(tags, drawing)
        }
    }
}
