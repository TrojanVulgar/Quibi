package com.easyplex.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.easyplex.R;
import com.easyplex.databinding.RegistrationSucessBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.util.Tools;

public class RegistrationSucess extends AppCompatActivity {

    RegistrationSucessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.registration_sucess);

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);

        // Load Mini Logo
        Tools.loadMiniLogo(binding.logoImageTop);

        // Load Toolbar
        Tools.loadToolbar(this,binding.toolbar,null);


        binding.btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationSucess.this, BaseActivity.class));
            finish();

        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
