package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {


    private List<Musica> listaMusicas;
    private Context context;
    private OnMusicaClickListener listener;

    // Interface para cliques nos itens
    public interface OnMusicaClickListener {
        void onMusicaClick(Musica musica, int position);
        void onOpcoesClick(Musica musica, int position, View view);
    }

    public MusicaAdapter(Context context, List<Musica> listaMusicas) {
        if (context == null) {
            throw new IllegalArgumentException("Context não pode ser null");
        }
        if (listaMusicas == null) {
            listaMusicas = new ArrayList<>();
        }
        this.context = context;
        this.listaMusicas = listaMusicas;
    }

    public void setOnMusicaClickListener(OnMusicaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_musica, parent, false);
        return new MusicaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = listaMusicas.get(position);

        holder.tvNomeMusica.setText(musica.getNome());
        holder.tvArtista.setText(musica.getArtista());
        holder.tvDuracao.setText(musica.getDuracao());

        // Carregar imagem da capa (usando Glide ou Picasso se necessário)
        if (musica.getCapaAlbum() != null && !musica.getCapaAlbum().isEmpty()) {
            // Aqui você pode usar Glide ou Picasso para carregar a imagem
            // Glide.with(context).load(musica.getCapaAlbum()).into(holder.ivCapaAlbum);
        } else {
            holder.ivCapaAlbum.setImageResource(R.drawable.album);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMusicaClick(musica, position);
            }
        });

        holder.btnOpcoes.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpcoesClick(musica, position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMusicas.size();
    }

    // Método para atualizar a lista
    public void updateList(List<Musica> novaLista) {
        this.listaMusicas = novaLista;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class MusicaViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCapaAlbum;
        TextView tvNomeMusica;
        TextView tvArtista;
        TextView tvDuracao;
        ImageButton btnOpcoes;

        public MusicaViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCapaAlbum = itemView.findViewById(R.id.iv_capa_album);
            tvNomeMusica = itemView.findViewById(R.id.tv_nome_musica);
            tvArtista = itemView.findViewById(R.id.tv_artista);
            tvDuracao = itemView.findViewById(R.id.tv_duracao);
            btnOpcoes = itemView.findViewById(R.id.btn_opcoes);
        }
    }
}