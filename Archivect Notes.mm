<map version="0.8.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1327740957207" ID="Freemind_Link_579136631" MODIFIED="1327740995692" TEXT="Archivect Notes">
<node CREATED="1327741061249" ID="_" MODIFIED="1327741062727" POSITION="right" TEXT="Finder">
<node CREATED="1327741063373" ID="Freemind_Link_1518532506" MODIFIED="1327741083608" TEXT="Has a Sources">
<node CREATED="1327741084405" ID="Freemind_Link_1605461299" MODIFIED="1327741092214" TEXT="Contains all the sources the user has specified"/>
<node CREATED="1327741092813" ID="Freemind_Link_1617702837" MODIFIED="1327741105441" TEXT="Maintains separate lists for">
<node CREATED="1327741105749" ID="Freemind_Link_1188974708" MODIFIED="1327741109049" TEXT="rooted sources"/>
<node CREATED="1327741109718" ID="Freemind_Link_1426948078" MODIFIED="1327741618320" TEXT="(e.g. /a/b/c or d:\dir\subdir or \\server\share\dir\dir1)"/>
<node CREATED="1327741124686" ID="Freemind_Link_1876224323" MODIFIED="1327741128961" TEXT="and unrooted sources"/>
<node CREATED="1327741129550" ID="Freemind_Link_870694193" MODIFIED="1327741140186" TEXT="(e.g. dir/dir2/dir3)"/>
</node>
<node CREATED="1327741141894" ID="Freemind_Link_765785419" MODIFIED="1327741161050" TEXT="sources are returned sorted">
<node CREATED="1327741311113" ID="Freemind_Link_1949219176" MODIFIED="1327741313420" TEXT="root order">
<node CREATED="1327741161623" ID="Freemind_Link_1186840230" MODIFIED="1327741174258" TEXT="rooted sources are sorted by root"/>
<node CREATED="1327741174823" ID="Freemind_Link_879815329" MODIFIED="1327741186882" TEXT="drive letters sorted together alphabetically"/>
<node CREATED="1327741187079" ID="Freemind_Link_744895114" MODIFIED="1327741197698" TEXT="unc paths sorted together alphabetically"/>
</node>
<node CREATED="1327741313785" ID="Freemind_Link_296246325" MODIFIED="1327741315756" TEXT="path order">
<node CREATED="1327741198815" ID="Freemind_Link_949600671" MODIFIED="1327741500262" TEXT="there are four sort containers">
<node CREATED="1327741451834" ID="Freemind_Link_116423706" MODIFIED="1327741486773" TEXT="unrooted paths"/>
<node CREATED="1327741501779" ID="Freemind_Link_1867496264" MODIFIED="1327741515806" TEXT="UNIX: rooted paths"/>
<node CREATED="1327741456434" ID="Freemind_Link_716001302" MODIFIED="1327741523822" TEXT="Windows: drive letters"/>
<node CREATED="1327741468818" ID="Freemind_Link_1377591590" MODIFIED="1327741527903" TEXT="Windows: \\server\share"/>
</node>
<node CREATED="1327741553299" ID="Freemind_Link_631110068" MODIFIED="1327741593240" TEXT="path order is applied in each of these"/>
<node CREATED="1327741228072" ID="Freemind_Link_1201847231" MODIFIED="1327741251111" TEXT="the path sort order is depth based"/>
<node CREATED="1327741251801" ID="Freemind_Link_852498791" MODIFIED="1327741271836" TEXT="paths near the top of the filesystem come first"/>
<node CREATED="1327741274185" ID="Freemind_Link_1722708336" MODIFIED="1327741281339" TEXT="then descend down into the filesystem"/>
<node CREATED="1327741358874" ID="Freemind_Link_1914976717" MODIFIED="1327741419240" TEXT="unrooted paths "/>
</node>
</node>
</node>
<node CREATED="1327741635109" ID="Freemind_Link_825089563" MODIFIED="1327741781587" TEXT="Has a store-of-path-components-that-have-been-processed">
<node CREATED="1327741710787" ID="Freemind_Link_1494619925" MODIFIED="1327741798917" TEXT="maintained as a tree of path components&#xa;with existence in the tree indicating that&#xa;they have been processed"/>
<node CREATED="1327741745580" ID="Freemind_Link_1198114" MODIFIED="1327741769207" TEXT="So I can know whether a dir/file has been archived or not"/>
</node>
</node>
</node>
</map>
