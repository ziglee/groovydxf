package net.cassiolandim.dxf

import net.cassiolandim.dxf.legacy.layouts.DXF12Layout
import net.cassiolandim.dxf.legacy.layouts.DXF12Layouts
import net.cassiolandim.dxf.legacy.LegacyDXFFactory
import net.cassiolandim.dxf.lldxf.Repair
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.sections.HeaderSection
import net.cassiolandim.dxf.sections.Sections
import net.cassiolandim.dxf.tools.JulianDate

class Drawing {

    def comments = [] // list of comment strings - saved as (999, comment) tags on top of file
    LegacyDXFFactory dxfFactory  // readonly - set by _bootstraphook()
    EntityDB entitydb
    Sections sections
    DXF12Layouts layouts

    Drawing(String templatePath = 'AC1009.dxf') {
        def inputStream = new FileReader(templatePath)//'cp1252'
        def tagReader = Tagger.streamTagger(inputStream)
        inputStream.close()

        entitydb = new EntityDB()
        sections = new Sections(tagReader, this)

        Repair.enableHandles(this)
        layouts = dxfFactory.getLayouts()

        sections.header.set('$TDCREATE', JulianDate.fromDate(new Date()))
    }

    def bootstrapHook(HeaderSection header, def comments) {
        // called from HeaderSection() object to update important dxf properties
        // before processing sections, which depends from this properties.
        this.comments = comments // preserve leading file comments
        String seed = header.get('$HANDSEED', entitydb.handles.toString())
        entitydb.handles.reset(seed)
        dxfFactory = new LegacyDXFFactory(this)
    }

    DXF12Layout getModelSpace() {
        return layouts.modelSpace
    }

    def getBlocks() {
        return sections.blocks
    }

    void save(String filename) {
        def stream = new File(filename)
        if (stream.exists())
            stream.delete()
        stream.createNewFile()
        updateMetadata()
        sections.write(stream)
    }

    private void updateMetadata() {
        sections.header.set('$TDUPDATE', JulianDate.fromDate(new Date()))
        sections.header.set('$HANDSEED', entitydb.handles.toString())
        sections.header.set('$DWGCODEPAGE', 'ANSI_1252')
    }

    HeaderSection getHeader() {
        return sections.header
    }

    def getActiveEntitySpaceLayoutKeys() {
        def layoutKeys = [getModelSpace().layoutKey]
        def activeLayoutKey = getActiveLayoutKey()
        if (activeLayoutKey)
            layoutKeys.add(activeLayoutKey)
        return layoutKeys
    }

    int getActiveLayoutKey() {
        return layout().layoutKey // AC1009 supports just one layout and this is the active one
    }

    DXF12Layout layout(String name = null) {
        return layouts.get(name)
    }


}
