package com.example.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retrofit.Interface.JsonApi;
import com.example.retrofit.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activi_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        //Le decimos al reciclerview que tiene un tamaño fijo (No depende del contenido del adapter)
        recyclerView.setHasFixedSize(true);
        //Un LayoutManager es responsable de medir y posicionar las vistas de elementos dentro de un RecyclerView
        //(LinearLayoutManager shows items in a vertical or horizontal scrolling list.)
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        getPosts(null);

        //Manage Swipe on items  -->
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        mAdapter.remove(viewHolder.getAdapterPosition());
                        //mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Actualizar el layout (Con simbolo de carga)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts(swipeRefreshLayout);//? Se supone que se tiene que refrescar sin los eliminados
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getPosts(final SwipeRefreshLayout myswipeRefreshLayout){
       Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")//Pagina base
                .addConverterFactory(GsonConverterFactory.create())//Indicamos que se quiere convertir a JSON
                .build();

        JsonApi jsonApi = retrofit.create(JsonApi.class);
        //Obtenemos los valores del GET(de la interfaz)
        Call<List<Post>> call= jsonApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if(!response.isSuccessful()){//En caso exitoso
                    Toast.makeText(getApplicationContext(),"Codigo: " + response.code(),Toast.LENGTH_LONG).show();
                    return;
                }

                List<Post> postsList = response.body();
                mAdapter = new MyAdapter(postsList);
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {//En caso de un error
                Toast.makeText(getApplicationContext(),"Error: " + t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}