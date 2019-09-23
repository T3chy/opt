package opt.data;

import jaxb.Link;
import jaxb.Roadparam;
import profiles.Profile1D;

public class LinkOfframp extends AbstractLink {

    /////////////////////////////////////
    // construction
    /////////////////////////////////////

    public LinkOfframp(Link link, Roadparam rp,int mng_lanes,FDparams mng_fd,boolean mng_barrier,boolean mng_separated) {
        super(link);
        this.params = new ParametersRamp(
                "",
                false,
                link.getFullLanes(),
                mng_lanes,
                mng_barrier,
                mng_separated,
                link.getLength(),
                rp.getCapacity(),
                rp.getJamDensity(),
                rp.getSpeed(),
                mng_fd==null ? Float.NaN : mng_fd.capacity_vphpl,
                mng_fd==null ? Float.NaN : mng_fd.jam_density_vpkpl,
                mng_fd==null ? Float.NaN : mng_fd.ff_speed_kph);
    }

    public LinkOfframp(long id, Segment mysegment, AbstractLink up_link, AbstractLink dn_link, Long start_node_id, Long end_node_id, ParametersRamp params) {
        super(id, mysegment, up_link, dn_link, start_node_id, end_node_id, params);
    }

    // used by clone
    public LinkOfframp(long id, Long start_node_id, Long end_node_id, AbstractParameters params){
        super(id,start_node_id,end_node_id,params);
        this.params.set_is_inner(((ParametersRamp)params).is_inner);
    }

    @Override
    public Type get_type() {
        return Type.offramp;
    }

    @Override
    public boolean is_ramp() {
        return true;
    }

    /////////////////////////////////////
    // up and dn segment
    /////////////////////////////////////

    @Override
    public Segment get_up_segment(){
        return up_link.get_up_segment();
    }

    /////////////////////////////////////
    // demands and splits
    /////////////////////////////////////

    @Override
    public void set_split(Long comm_id, Profile1D profile) throws Exception {
        this.splits.put(comm_id,profile);
    }

    /////////////////////////////////////
    // insert
    /////////////////////////////////////

    @Override
    public Segment insert_dn_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {

        if(dn_link!=null)
            return null;
        assert(ramp_params==null);

        Segment dn_segment = get_dn_segment();

        // create new dnstream link
        LinkConnector new_link = (LinkConnector) create_dn_FwyOrConnLink(Type.connector,fwy_params);

        // wrap in a segment
        Segment new_segment = create_segment(new_link,seg_name);

        // connect dnstream segment to new node
        if(dn_segment!=null) {
            connect_segments_upstr_node_to(dn_segment, new_link.end_node_id);
            new_link.dn_link = dn_segment.fwy;
            dn_segment.fwy.up_link = new_link;
        }

        return new_segment;
    }

    @Override
    public Segment insert_up_segment(String seg_name, ParametersFreeway fwy_params, ParametersRamp ramp_params) {
        return null;
    }

    @Override
    protected boolean is_permitted_uplink(AbstractLink link) {
        return link instanceof LinkFreeway;
    }

    @Override
    protected boolean is_permitted_dnlink(AbstractLink link) {
        return link instanceof LinkConnector;
    }
}
