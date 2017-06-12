/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paymentwall.pandapptest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentwall.pandapptest.R;
import com.paymentwall.pandapptest.config.Constants;
import com.paymentwall.pandapptest.config.SharedPreferenceManager;


public class LLandActivity extends Activity {
    LLand mLand;
    private TextView tvMana, tvShield, tvGem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lland);
        mLand = (LLand) findViewById(R.id.world);
        mLand.setScoreField((TextView) findViewById(R.id.score));
        mLand.setHighScoreField((TextView)findViewById(R.id.highscore));
        mLand.setItemsField((LinearLayout)findViewById(R.id.llItems));
        mLand.setSplash(findViewById(R.id.welcome));

        tvMana = (TextView)findViewById(R.id.tvMana);
        tvShield = (TextView)findViewById(R.id.tvShield);
        tvGem = (TextView)findViewById(R.id.tvGem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvMana.setText(SharedPreferenceManager.getGameItemQuantity(this, Constants.ITEM_MANA_ID) + "");
        tvShield.setText(SharedPreferenceManager.getGameItemQuantity(this, Constants.ITEM_SHIELD_ID) + "");
        tvGem.setText(SharedPreferenceManager.getGameItemQuantity(this, Constants.ITEM_GEM_ID) + "");
    }

    @Override
    public void onPause() {
        mLand.stop();
        super.onPause();
    }

    public void onClickItem(View v) {
        Intent intent = new Intent(LLandActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
