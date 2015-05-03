package coursera.vortex.dailyselfie;

import coursera.vortex.dailyselfie.MainActivity.ImageListFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class DeleteImageItemDialogFragment extends DialogFragment {
	private static int mItemToRemove;
	
	public static DeleteImageItemDialogFragment newInstance(int itemToRemove) {
		
		mItemToRemove = itemToRemove;
		return new DeleteImageItemDialogFragment();
	}

	// Build AlertDialog using AlertDialog.Builder
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setMessage("Delete the selected element?")
				
				// Set up Yes Button
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, int id) {
								deleteElement(true);
							}
						})
				
				// Set up No Button
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteElement(false);
							}
						}).create();
	}
	
	private void deleteElement(boolean shouldDelete) {
		if (shouldDelete) {
			Intent i = new Intent();
			i.putExtra("numToDelete", mItemToRemove);
			
			getTargetFragment().onActivityResult(getTargetRequestCode(), ImageListFragment.DISMISS_VALUE, i);
			this.dismiss();
		
		} else {
			// Dismiss dialog w/o removing the item
			this.dismiss();
		}
	}
}
