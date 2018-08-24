package project.database.element;

public class TagCollection extends Collection<TagObject> {
    public TagCollection() {
        super();
    }
    public TagCollection(TagCollection collection) {
        super(collection);
    }

    public boolean contains(TagObject object) {
        if (object.equals(null)) return false;
        String group = object.getGroup();
        String name = object.getName();
        for (Object iterator : this) {
            String iteratorGroup = ((TagObject) iterator).getGroup();
            String iteratorName = ((TagObject) iterator).getName();
            if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                return true;
            }
        }
        return false;
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
