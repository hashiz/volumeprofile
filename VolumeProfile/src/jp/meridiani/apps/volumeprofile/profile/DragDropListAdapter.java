package jp.meridiani.apps.volumeprofile.profile;

public interface DragDropListAdapter {

	// @param srcPos move this item
	// @param dstPos insert item to above
	public boolean moveItem(int srcPos, int dstPos);

}
