package opt.data.control;

public class Sensor {

    public long id;
    public long link_id;
    public float offset;
    public AbstractController myController;

    public Sensor(long id, long link_id, float offset, AbstractController myController) {
        this.id = id;
        this.link_id = link_id;
        this.offset = offset;
        this.myController = myController;
    }

    public Sensor(jaxb.Sensor j) {
        this.id = j.getId();
        this.link_id = j.getLinkId();
        this.offset = j.getPosition();
        this.link_id = j.getLinkId();
    }

    public jaxb.Sensor to_jaxb(){
        jaxb.Sensor j = new jaxb.Sensor();
        j.setId(id);
        j.setDt(myController.dt);
        j.setPosition(offset);
        j.setLinkId(link_id);
        j.setType("loop");
        return j;
    }
}
