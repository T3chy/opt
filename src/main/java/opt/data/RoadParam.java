package opt.data;

import java.util.Objects;

public class RoadParam {
    Long id;
    float capacity;
    float speed;
    float jam_density;

    public RoadParam(float capacity, float speed, float jam_density) {
        this.capacity = capacity;
        this.speed = speed;
        this.jam_density = jam_density;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadParam roadParam = (RoadParam) o;
        return Float.compare(roadParam.capacity, capacity) == 0 &&
                Float.compare(roadParam.speed, speed) == 0 &&
                Float.compare(roadParam.jam_density, jam_density) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, speed, jam_density);
    }
}