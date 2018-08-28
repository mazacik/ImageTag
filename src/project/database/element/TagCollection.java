package project.database.element;

public class TagCollection extends Collection<TagObject> {
    public TagCollection() {
        super();
    }
    public TagCollection(TagCollection collection) {
        super(collection);
    }

    public boolean contains(TagObject object) {
        if (object == null) return false;
        if (super.contains(object)) return true;
        else {
            String group = object.getGroup();
            String name = object.getName();
            for (TagObject iterator : this) {
                String iteratorGroup = iterator.getGroup();
                String iteratorName = iterator.getName();
                if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                    return true;
                }
            }
            return false;
        }
    }
    public void sort() {
        super.sort(TagObject.getComparator());
    }

    public TagCollection getIntersection(TagCollection collection) {
        TagCollection value = new TagCollection(collection);
        for (Object iterator : collection) {
            if (!this.contains(iterator)) {
                value.remove(iterator);
            }
        }
        return value;
    }
}
