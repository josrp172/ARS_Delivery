package com.thesis.tipqc.ars_delivery.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.thesis.tipqc.ars_delivery.BusinessOwner.DataManipulationUI.Delivery.addDelivery_step4_MAPS;

import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Progress bar task for change time progress of progress bar when solver is solving.
 *
 * @author Tomas David
 */
/*
 * Copyright 2015 Tomas David
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ProgressBarTask extends AsyncTask<Integer, Integer, Void> {

    /**
     * Class tag.
     */
    private static final String TAG = "ProgressBarTask";

    /**
     * One second = 1000 milliseconds
     */
    private static final int SECOND = 1000;

    private static int MAX;

    /**
     * Actual progress of progress bar.
     */
    private int progress = 0;


    private ProgressBar progressBar;

    /**
     * Vrp fragment where progress bar is placed.
     */
    private addDelivery_step4_MAPS activity;

    /**
     * Constructor of progress bar task.
     * @param activity Vrp fragment where progress bar is placed.
     */
    public ProgressBarTask(addDelivery_step4_MAPS activity) {
        this.activity = activity;
        this.progress = 0;
        this.progressBar = activity.progress;
        MAX = this.progressBar.getMax();
    }

    @Override
    protected void onPreExecute() {
        progressBar.setProgress(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Void doInBackground(Integer... params) {
        // while solver task is running change progress every 1 second
        while (activity.getVrpSolverTask().isRunning() && progress < 100) {
            try {
                Thread.sleep(SECOND);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread sleep error.", e);
            }
            ++progress;
            publishProgress(progress);
        }

        return null;
    }



    @Override
    protected void onProgressUpdate(Integer... solutions) {
        progressBar.setProgress(progress);
    }

    @Override
    protected void onPostExecute(Void solution) {

    }
}
