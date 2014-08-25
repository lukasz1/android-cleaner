package co.qualityc.cleaner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import java.util.ArrayList;

import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.R;
import co.qualityc.cleaner.SCIntent;
import co.qualityc.cleaner.clean.receiver.CleanReceiver;
import co.qualityc.cleaner.clean.service.SimpleCleanService;
import co.qualityc.cleaner.fragment.FullCleanResultsFragment;
import co.qualityc.cleaner.fragment.ScanFragment;
import co.qualityc.cleaner.scan.ProcessInfo;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;
import co.qualityc.cleaner.scan.receiver.ScanReceiver;


public class CleanerActivity extends ActionBarActivity
        implements ScanReceiver.ScanListener, CleanReceiver.CleanListener {
    private static final String TAG = CleanerActivity.class.getName();
    public static final String ACTION_BACKGROUND_SCAN = "cleaner.action.backgroundScan";

    private ProgressDialog progressDialog;

    private MemoryScanResult memoryScanResult;
    private StorageScanResult storageScanResult;

    private ScanReceiver scanReceiver = new ScanReceiver();
    private CleanReceiver cleanReceiver = new CleanReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        initIntent();
        initActionBar();
        initViews();

        if (savedInstanceState == null) {
            showScanFragment(true);
        }
    }

    private void initIntent() {
        if (hasBackgroundScanIntent()) {
            memoryScanResult = getIntent().getParcelableExtra(SCIntent.KEY_MEMORY_SCAN_RESULT);
            storageScanResult = (StorageScanResult)
                    getIntent().getSerializableExtra(SCIntent.KEY_STORAGE_SCAN_RESULT);
        }
    }

    private boolean hasBackgroundScanIntent() {
        return getIntent() != null && getIntent().getAction() != null &&
                getIntent().getAction().equals(ACTION_BACKGROUND_SCAN);
    }

    protected void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Optimising..");
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Clean your device");
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanReceiver.register(this, this);
        cleanReceiver.register(this, this);
    }

    @Override
    protected void onPause() {
        scanReceiver.unregister(this);
        cleanReceiver.unregister(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStartScan() {
        showScanFragment(false);
    }

    @Override
    public void onScanFinished(
            MemoryScanResult memoryScanResult,
            StorageScanResult storageScanResult) {
        this.memoryScanResult = memoryScanResult;
        this.storageScanResult = storageScanResult;
    }

    @Override
    public void onCleanFinished(MemoryScanResult memoryScanResult,
                                StorageScanResult storageScanResult) {
        hideProgressDialog();

        showCleanResultsFragment(memoryScanResult, storageScanResult);
    }

    public void onCleanDevice() {
        cleanDevice();
    }

    public void cleanDevice() {
        Intent cleanDeviceIntent = new Intent(this, SimpleCleanService.class);

        cleanDeviceIntent.putExtra(SimpleCleanService.KEY_APP_PROCESSES,
                new ArrayList<ProcessInfo>(memoryScanResult
                        .getRunningProcesses())
        );
        cleanDeviceIntent.putExtra(SimpleCleanService.KEY_JUNK_PACKAGES,
                new ArrayList<PackageInfo>(storageScanResult.getJunkPackagesInfo()));


        startService(cleanDeviceIntent);

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showScanFragment(boolean showArtificialProgress) {
        replaceFragment(ScanFragment.newInstance(showArtificialProgress),
                ScanFragment.TAG);
    }

    private void showCleanResultsFragment(MemoryScanResult memoryScanResult,
                                          StorageScanResult storageScanResult) {
        replaceFragment(FullCleanResultsFragment.newInstance(memoryScanResult, storageScanResult),
                FullCleanResultsFragment.TAG);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out, android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .replace(
                        R.id.content,
                        fragment,
                        tag)
                .commit();
    }
}
