package opt.data;

import opt.UserSettings;
import profiles.Profile1D;
import utils.OTMUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class FreewayScenario {

    private Long max_link_id;
    private Long max_node_id;
    private Long max_seg_id;

    public String name;
    protected Scenario scenario;
    protected Map<Long,Segment> segments = new HashMap<>();

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public FreewayScenario() {
    }

    public FreewayScenario(String scnname, String segmentname, ParametersFreeway params){
        this.name = scnname;
        max_link_id = -1l;
        max_node_id = -1l;
        max_seg_id = -1l;
        scenario = new Scenario();
        create_isolated_segment(segmentname,params, AbstractLink.Type.freeway);
        scenario.commodities.put(0l,new Commodity(0l,"Unnamed commodity",1f));
    }

    public FreewayScenario(String name,jaxb.Lnks jaxb_lnks,jaxb.Sgmts jaxb_segments,jaxb.Scenario jaxb_scenario) throws Exception {

        this.name = name;

        // create Scenario object
        this.scenario = new Scenario(jaxb_scenario);

        // attach link names
        if (jaxb_lnks!=null)
            for(jaxb.Lnk lnk : jaxb_lnks.getLnk())
                if (scenario.links.containsKey(lnk.getId())) {
                    AbstractLink link = scenario.links.get(lnk.getId());
                    link.set_name(lnk.getName());

                    // TODO REMOVE THESE AFTER UPDATING EXISTING FILES.
                    if(!link.params.has_mng()){
                        link.set_mng_lanes(lnk.getManagedLanes()==null ? 0 : lnk.getManagedLanes().intValue());
                        link.set_mng_barrier(lnk.isManagedLanesBarrier()==null ? false : lnk.isManagedLanesBarrier());
                        link.set_mng_separated(lnk.isManagedLanesSeparated()==null ? false : lnk.isManagedLanesSeparated());
                    }
                    if(!link.params.has_aux()){
                        link.set_aux_lanes(lnk.getAuxLanes()==null ? 0 : lnk.getAuxLanes().intValue());
                    }
                    link.set_is_inner(lnk.isIsInner()==null ? false : lnk.isIsInner());

                    // TODO REMOVE THIS
                    // HACK SET MNG AND AUX LANE PARAMETERS IF THEY ARE NOT SET
                    // ---------------------------------------------------------------------------
                    if(link.params.has_mng()){
                        if(link.params.mng_fd==null)
                            link.params.mng_fd = new FDparams(
                                    (float)UserSettings.defaultManagedLaneCapacityVph,
                                    (float)UserSettings.defaultManagedLaneJamDensityVpk,
                                    (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph);
                        if(Float.isNaN(link.params.mng_fd.capacity_vphpl))
                            link.params.mng_fd.capacity_vphpl = (float)UserSettings.defaultManagedLaneCapacityVph;
                        if(Float.isNaN(link.params.mng_fd.jam_density_vpkpl))
                            link.params.mng_fd.jam_density_vpkpl = (float)UserSettings.defaultManagedLaneJamDensityVpk;
                        if(Float.isNaN(link.params.mng_fd.ff_speed_kph))
                            link.params.mng_fd.ff_speed_kph = (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    if(link.params.has_aux()){
                        FDparams aux_fd = ((ParametersFreeway) link.params).aux_fd;
                        if(aux_fd==null)
                            aux_fd = new FDparams(
                                    (float)UserSettings.defaultAuxLaneCapacityVph,
                                    (float)UserSettings.defaultAuxLaneJamDensityVpk,
                                    (float)UserSettings.defaultAuxLaneFreeFlowSpeedKph);
                        if(Float.isNaN(aux_fd.capacity_vphpl))
                            aux_fd.capacity_vphpl = (float)UserSettings.defaultManagedLaneCapacityVph;
                        if(Float.isNaN(aux_fd.jam_density_vpkpl))
                            aux_fd.jam_density_vpkpl = (float)UserSettings.defaultManagedLaneJamDensityVpk;
                        if(Float.isNaN(aux_fd.ff_speed_kph))
                            aux_fd.ff_speed_kph = (float)UserSettings.defaultManagedLaneFreeFlowSpeedKph;
                    }
                    // ------------------------------------------------------------------

                }

        // create segments
        long max_sgmt_id = 0;
        if(jaxb_segments!=null)
            for (jaxb.Sgmt sgmt : jaxb_segments.getSgmt()) {
                Segment segment = new Segment(this,max_sgmt_id++, sgmt);
                segments.put(segment.id,segment);
            }

        // make link connections
        for(AbstractLink abslink : scenario.links.values()){

            Set<AbstractLink> up_links = scenario.nodes.get(abslink.start_node_id).in_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment!=null )
                    .collect(Collectors.toSet());

            Set<AbstractLink> dn_links = scenario.nodes.get(abslink.end_node_id).out_links.stream()
                    .map(link_id -> scenario.links.get(link_id))
                    .filter(link -> link.mysegment!=null )
                    .collect(Collectors.toSet());

            Set<AbstractLink> up_links_f = null;
            Set<AbstractLink> dn_links_f = null;
            switch(abslink.get_type()){

                case freeway:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case connector:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkOfframp)
                            .map(link -> (LinkOfframp)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkOnramp)
                            .map(link -> (LinkOnramp)link)
                            .collect(Collectors.toSet());

                    break;

                case onramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    break;

                case offramp:

                    up_links_f = up_links.stream()
                            .filter(link -> link instanceof LinkFreeway)
                            .map(link -> (LinkFreeway)link)
                            .collect(Collectors.toSet());

                    dn_links_f = dn_links.stream()
                            .filter(link -> link instanceof LinkConnector)
                            .map(link -> (LinkConnector)link)
                            .collect(Collectors.toSet());

                    break;
            }

            if(up_links_f!=null && !up_links_f.isEmpty())
                abslink.up_link = up_links_f.iterator().next();

            if(dn_links_f!=null && !dn_links_f.isEmpty())
                abslink.dn_link = dn_links_f.iterator().next();

        }

        // assign demands
        if (jaxb_scenario.getDemands()!=null)
            for(jaxb.Demand dem : jaxb_scenario.getDemands().getDemand())
                scenario.links.get(dem.getLinkId()).set_demand_vph(
                        dem.getCommodityId(),
                        new Profile1D(
                                dem.getStartTime(),
                                dem.getDt(),
                                OTMUtils.csv2list(dem.getContent())));

        // assign splits
        if (jaxb_scenario.getSplits()!=null)
            for(jaxb.SplitNode jsplitnode : jaxb_scenario.getSplits().getSplitNode())
                for(jaxb.Split split : jsplitnode.getSplit()) {
                    AbstractLink link = scenario.links.get(split.getLinkOut());
                    if( link instanceof LinkOfframp )
                        ((LinkOfframp)link).set_split(
                                jsplitnode.getCommodityId(),
                                new Profile1D(
                                        jsplitnode.getStartTime(),
                                        jsplitnode.getDt(),
                                        OTMUtils.csv2list(split.getContent())));
                }

        // max ids
        reset_max_ids();

    }

    public FreewayScenario clone(){
        FreewayScenario scn_cpy = new FreewayScenario();
        scn_cpy.name = name;
        scn_cpy.max_link_id = max_link_id;
        scn_cpy.max_node_id = max_node_id;
        scn_cpy.max_seg_id = max_seg_id;
        scn_cpy.scenario = scenario.clone();
        scn_cpy.segments = new HashMap<>();
        for(Map.Entry<Long,Segment> e : segments.entrySet()) {
            Segment new_segment = e.getValue().clone();
            new_segment.fwy_scenario = scn_cpy;
            scn_cpy.segments.put(e.getKey(),new_segment );
        }

        // make link connections
        for(AbstractLink link : scenario.links.values()){
            AbstractLink new_link = scn_cpy.scenario.links.get(link.id);
            if(link.mysegment!=null)
                new_link.mysegment = scn_cpy.segments.get(link.mysegment.id);
            if(link.up_link!=null)
                new_link.up_link = scn_cpy.scenario.links.get(link.up_link.id);
            if(link.dn_link!=null)
                new_link.dn_link = scn_cpy.scenario.links.get(link.dn_link.id);
        }

        return scn_cpy;
    }

    /////////////////////////////////////
    // scenario getters
    /////////////////////////////////////

    public List<AbstractLink> get_links(){
        return segments.values().stream()
                .flatMap(sgmt->sgmt.get_links().stream())
                .collect(toList());
    }

    public Map<Long,Node> get_nodes(){
        return scenario.nodes;
    }

    /////////////////////////////////////
    // segment getters
    /////////////////////////////////////

    public List<List<Segment>> get_linear_freeway_segments(){

        List<List<Segment>> result = new ArrayList<>();

        // collect all freeway segments (ignore connectors)
        List<Segment> all_segments = segments.values().stream()
                .filter(sgmt->sgmt.fwy instanceof LinkFreeway )
                .collect(toList());

        // first extract linear freeways
        List<Segment> source_segments = segments.values().stream()
                .filter(sgmt->sgmt.fwy.is_source())
                .collect(toList());

        for(Segment source_segment : source_segments){
            List<Segment> this_fwy = build_freeway_from_segment(source_segment);
            result.add(this_fwy);
            all_segments.removeAll(this_fwy);
        }

        // the remaining segments should all be circular

        // sort all remaining segments by id
        Collections.sort(all_segments,Comparator.comparing(Segment::get_id));
        while(!all_segments.isEmpty()){
            List<Segment> this_fwy = build_freeway_from_segment(all_segments.get(0));
            result.add(this_fwy);
            all_segments.removeAll(this_fwy);
        }

        // sort all freeways by the id of their first segment
        result.sort((List<Segment> o1,List<Segment> o2)->(int) (o1.get(0).get_id()-o2.get(0).get_id()));

        return result;
    }

    public List<LinkConnector> get_connectors(){
        List<LinkConnector> result = segments.values().stream()
                .filter(sgmt->sgmt.fwy instanceof LinkConnector )
                .map(sgmt -> (LinkConnector) sgmt.fwy )
                .collect(toList());
        Collections.sort(result,Comparator.comparing(AbstractLink::get_id));
        return result;
    }

    /**
     * get all segments in this scenario
     * @return Collection of segments
     */
    public Set<Segment> get_segments(){
        return new HashSet<>(segments.values());
    }

    /**
     * Get an order list of the names of segments
     * @return
     */
    public Set<String> get_segment_names(){
        return segments.values().stream().map(segment->segment.name).collect(Collectors.toSet());
    }

    public Segment get_segment_by_name(String name){
        Set<Segment> xsegments = segments.values().stream()
                .filter(seg->seg.name.equals(name))
                .collect(toSet());
        return xsegments.size()==0 ? null : xsegments.iterator().next();
    }

    /**
     * Get a segment with its id
     * @return
     */
    public Segment get_segment_with_id(Long id){
        return segments.containsKey(id) ? segments.get(id) : null;
    }

    /////////////////////////////////////
    // segment create / delete
    /////////////////////////////////////

    /**
     * Create an isolated segment
     * @return A new segment
     */
    public Segment create_isolated_segment(String segment_name, ParametersFreeway params, AbstractLink.Type linktype){

        if(linktype!=AbstractLink.Type.freeway && linktype!= AbstractLink.Type.connector)
            return null;

        // create a segment
        Long segment_id = ++max_seg_id;
        Segment segment = new Segment();
        segment.id = segment_id;
        segment.name = segment_name;
        segment.fwy_scenario = this;
        segments.put(segment_id,segment);

        // create nodes and freeway link
        Node start_node = new Node(++max_node_id);
        Node end_node = new Node(++max_node_id);

        LinkFreewayOrConnector link = null;
        switch(linktype){
            case freeway:

                link = new LinkFreeway(
                        ++max_link_id,   // id,
                        segment,// mysegment,
                        null,// up_link,
                        null,// dn_link,
                        start_node.id,// start_node_id,
                        end_node.id,// end_node_id,
                        params);

                break;
            case connector:

                link = new LinkConnector(
                        ++max_link_id,// id,
                        segment,// mysegment,
                        null,// up_link,
                        null,// dn_link,
                        start_node.id,// start_node_id,
                        end_node.id,// end_node_id,
                        params);

                break;
        }

        start_node.out_links.add(link.id);
        end_node.in_links.add(link.id);
        segment.fwy = link;

        // add to the scenario
        scenario.nodes.put(start_node.id,start_node);
        scenario.nodes.put(end_node.id,end_node);
        scenario.links.put(link.id,link);

        return segment;
    }

    /**
     * Delete a segment
     * @param segment
     */
    public void delete_segment(Segment segment) throws Exception {

        if(segments.size()==1)
            throw new Exception("Removing the sole segment is not allowed.");

        // disconnect ramps from connectors, or delete nodes
        // delete links
        for(LinkOnramp or : segment.get_ors())
            if (or.up_link == null)
                scenario.nodes.remove(or.start_node_id);
            else {
                or.up_link.dn_link = null;
                scenario.nodes.get(or.up_link.end_node_id).out_links.remove(or.id);
            }

        for(LinkOfframp fr : segment.get_frs())
            if (fr.dn_link == null)
                scenario.nodes.remove(fr.end_node_id);
            else {
                fr.dn_link.up_link = null;
                scenario.nodes.get(fr.dn_link.start_node_id).in_links.remove(fr.id);
            }

        // disconnect fwy, or delete nodes
        if(segment.fwy.up_link==null)
            scenario.nodes.remove(segment.fwy.start_node_id);
        else {
            segment.fwy.up_link.dn_link = null;
            scenario.nodes.get(segment.fwy.up_link.end_node_id).out_links.remove(segment.fwy.id);
        }

        if(segment.fwy.dn_link==null)
            scenario.nodes.remove(segment.fwy.end_node_id);
        else {
            segment.fwy.dn_link.up_link = null;
            scenario.nodes.get(segment.fwy.dn_link.start_node_id).in_links.remove(segment.fwy.id);
        }

        // delete all links
        for(AbstractLink link : segment.get_links()) {
            link.demands = null;
            if(link instanceof LinkOfframp)
                ((LinkOfframp)link).splits = null;
            scenario.links.remove(link.id);
        }

        // delete the segment
        segment.in_ors = null;
        segment.out_ors = null;
        segment.in_frs = null;
        segment.out_frs = null;
        segments.remove(segment.id);

    }

    /////////////////////////////////////
    // commodity getters and setters
    /////////////////////////////////////

    /**
     * Retrieve a map of id -> commodity
     * @return Map<String,Commodity>
     */
    public Map<Long, Commodity> get_commodities(){
        return scenario.commodities;
    }

    /**
     * Get the commodity for a given name
     * @param name
     * @return Commodity or null
     */
    public Commodity get_commodity_by_name(String name){
        Optional<Commodity> x = scenario.commodities.values().stream()
                .filter(c->c.name.equals(name))
                .findFirst();
        return x.isPresent() ? x.get() : null;
    }

    /**
     * Create a new commodity
     * @param name
     * @return
     */
    public Commodity create_commodity(String name, float pvequiv){
        long max_id;
        if(scenario.commodities.isEmpty())
            max_id = 0;
        else
            max_id = scenario.commodities.values().stream().mapToLong(x->x.id).max().getAsLong() + 1;

        Commodity new_comm = new Commodity(max_id,name,pvequiv);
        scenario.commodities.put(new_comm.id,new_comm);
        return new_comm;
    }

    /**
     * Delete a commodity with a given name
     * @param name
     * @return
     */
    public boolean delete_commodity_with_name(String name){
        Optional<Long> comm_id = scenario.commodities.values().stream()
                .filter(c->c.name.equals(name))
                .map(c->c.id)
                .findFirst();

        if(comm_id.isPresent() && scenario.commodities.containsKey(comm_id.get())) {
            scenario.commodities.remove(comm_id.get());
            return true;
        }
        else
            return false;
    }

    /**
     * Delete a commodity with a given id
     * @param comm_id
     * @return
     */
    public boolean delete_commodity_with_id(Long comm_id){
        if( scenario.commodities.containsKey(comm_id) ){
            scenario.commodities.remove(comm_id);
            return true;
        }
        else
            return false;
    }

    /////////////////////////////////////
    // utilities
    /////////////////////////////////////

    public boolean is_valid_segment_name(String name){
        return !segments.values().stream().anyMatch(seg->seg.name.equals(name));
    }

    public boolean is_valid_link_name(String name){
        return !scenario.links.values().stream()
                .anyMatch(link->link.get_name()!=null && link.get_name().equals(name));
    }

    /////////////////////////////////////
    // run
    /////////////////////////////////////

    public void run_on_new_thread() throws Exception {
        throw new Exception("NOT IMPLEMENTED!");
    }

    /////////////////////////////////////
    // protected and private
    /////////////////////////////////////

    protected jaxb.Scn to_jaxb() throws Exception {
        jaxb.Scn scn = new jaxb.Scn();

        scn.setScenario(scenario.to_jaxb());

        scn.setName(name);

        jaxb.Sgmts sgmts = new jaxb.Sgmts();
        scn.setSgmts(sgmts);
        for(Segment segment : segments.values())
            sgmts.getSgmt().add(segment.to_jaxb());

        jaxb.Lnks lnks = new jaxb.Lnks();
        scn.setLnks(lnks);
        List<AbstractLink> all_links = get_links();
        Collections.sort(all_links);
        for(AbstractLink link : all_links ){
            if(link==null)
                continue;
            jaxb.Lnk lnk = new jaxb.Lnk();
            lnk.setId(link.id);
            lnk.setName(link.get_name());
//            lnk.setManagedLanes(BigInteger.valueOf(link.get_mng_lanes()));
//            lnk.setManagedLanesBarrier(Boolean.valueOf(link.get_mng_barrier()));
//            lnk.setManagedLanesSeparated(Boolean.valueOf(link.get_mng_separated()));
//            lnk.setAuxLanes(BigInteger.valueOf(link.get_aux_lanes()));
            lnk.setIsInner(link.get_is_inner());
            lnks.getLnk().add(lnk);
        }
        return scn;
    }

    protected AbstractLink get_link(Long id){
        return scenario.links.get(id);
    }

    protected void reset_max_ids(){

        // link
        Optional<Long> opt_max_link_id = scenario.links.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_link_id = opt_max_link_id.isPresent() ? opt_max_link_id.get() : 0l;

        // node
        Optional<Long> opt_max_node_id = scenario.nodes.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_node_id = opt_max_node_id.isPresent() ? opt_max_node_id.get() : 0l;

        // segment
        Optional<Long> opt_max_seg_id = segments.keySet().stream()
                .max(Comparator.comparing(Long::valueOf));
        max_seg_id = opt_max_seg_id.isPresent() ? opt_max_seg_id.get() : 0l;

    }

    protected long new_link_id(){
        return ++max_link_id;
    }

    protected long new_node_id(){
        return ++max_node_id;
    }

    protected long new_seg_id(){
        return ++max_seg_id;
    }

    private List<Segment> build_freeway_from_segment(Segment first){
        List<Segment> this_fwy = new ArrayList<>();
        Segment curr_segment = first;
        this_fwy.add(first);
        while(curr_segment.fwy.dn_link!=null && !this_fwy.contains(curr_segment.fwy.dn_link.mysegment) ){
            curr_segment = curr_segment.fwy.dn_link.mysegment;
            this_fwy.add(curr_segment);
        }
        return this_fwy;
    }

    /////////////////////////////////////
    // override
    /////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreewayScenario that = (FreewayScenario) o;
        return name.equals(that.name) &&
                scenario.equals(that.scenario) &&
                segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, scenario, segments);
    }
}
