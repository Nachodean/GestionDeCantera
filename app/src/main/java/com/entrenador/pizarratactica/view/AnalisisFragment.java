package com.entrenador.pizarratactica.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.AnalisisController;
import com.entrenador.pizarratactica.model.InformeRival;
import com.entrenador.pizarratactica.model.OpcionesAnalisis;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Vista del Analisis Rival (UEFA A): formulario por fases del juego.
 * La logica y el guardado estan en {@link AnalisisController}.
 */
public class AnalisisFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private AutoCompleteTextView spSistema, spOfeGen, spOfeDef, spOfeMed, spOfeDel;
    private AutoCompleteTextView spDefGen, spDefDef, spDefMed, spDefDel;
    private AutoCompleteTextView spTranDA, spTranAD, spAbpOfe, spAbpDef;
    private TextInputEditText etFortalezas, etDebilidades, etObsOfe, etObsDef, etObsAbp;
    private TextInputEditText etJugadorPeligroso, etEslabonDebil;
    private MaterialButton btnGuardar;
    private View btnVerInformes;

    // Controlador (MVC)
    private AnalisisController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_analisis, container, false);
        recogerElementos(root);
        configurarDesplegables();
        establecerEscuchadores();
        return root;
    }

    private void recogerElementos(View root) {
        spSistema = root.findViewById(R.id.sp_sistema);
        spOfeGen = root.findViewById(R.id.sp_ofe_gen);
        spOfeDef = root.findViewById(R.id.sp_ofe_def);
        spOfeMed = root.findViewById(R.id.sp_ofe_med);
        spOfeDel = root.findViewById(R.id.sp_ofe_del);
        spDefGen = root.findViewById(R.id.sp_def_gen);
        spDefDef = root.findViewById(R.id.sp_def_def);
        spDefMed = root.findViewById(R.id.sp_def_med);
        spDefDel = root.findViewById(R.id.sp_def_del);
        spTranDA = root.findViewById(R.id.sp_tran_da);
        spTranAD = root.findViewById(R.id.sp_tran_ad);
        spAbpOfe = root.findViewById(R.id.sp_abp_ofe);
        spAbpDef = root.findViewById(R.id.sp_abp_def);

        etFortalezas = root.findViewById(R.id.et_fortalezas);
        etDebilidades = root.findViewById(R.id.et_debilidades);
        etObsOfe = root.findViewById(R.id.et_obs_ofe);
        etObsDef = root.findViewById(R.id.et_obs_def);
        etObsAbp = root.findViewById(R.id.et_obs_abp);
        etJugadorPeligroso = root.findViewById(R.id.et_jugador_peligroso);
        etEslabonDebil = root.findViewById(R.id.et_eslabon_debil);

        btnGuardar = root.findViewById(R.id.btn_guardar_analisis);
        btnVerInformes = root.findViewById(R.id.btn_ver_informes);
    }

    private void establecerEscuchadores() {
        controlador = new AnalisisController(this);
        btnGuardar.setOnClickListener(v -> controlador.alPulsarGuardar());
        if (btnVerInformes != null) btnVerInformes.setOnClickListener(v -> controlador.alPulsarVerInformes());
    }

    private void configurarDesplegables() {
        opciones(spSistema, OpcionesAnalisis.SISTEMAS);
        opciones(spOfeGen, OpcionesAnalisis.OFE_GEN);
        opciones(spOfeDef, OpcionesAnalisis.OFE_DEF);
        opciones(spOfeMed, OpcionesAnalisis.OFE_MED);
        opciones(spOfeDel, OpcionesAnalisis.OFE_DEL);
        opciones(spDefGen, OpcionesAnalisis.DEF_GEN);
        opciones(spDefDef, OpcionesAnalisis.DEF_DEF);
        opciones(spDefMed, OpcionesAnalisis.DEF_MED);
        opciones(spDefDel, OpcionesAnalisis.DEF_DEL);
        opciones(spTranDA, OpcionesAnalisis.TRAN_DA);
        opciones(spTranAD, OpcionesAnalisis.TRAN_AD);
        opciones(spAbpOfe, OpcionesAnalisis.ABP_OFE);
        opciones(spAbpDef, OpcionesAnalisis.ABP_DEF);
    }

    private void opciones(AutoCompleteTextView desplegable, String[] valores) {
        desplegable.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, valores));
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    /** Lee todo el formulario con las claves del informe. */
    public Map<String, String> recogerDatosFormulario() {
        Map<String, String> d = new LinkedHashMap<>();
        d.put(InformeRival.SISTEMA, spSistema.getText().toString());
        d.put(InformeRival.OFE_GEN, spOfeGen.getText().toString());
        d.put(InformeRival.OFE_DEF, spOfeDef.getText().toString());
        d.put(InformeRival.OFE_MED, spOfeMed.getText().toString());
        d.put(InformeRival.OFE_DEL, spOfeDel.getText().toString());
        d.put(InformeRival.DEF_GEN, spDefGen.getText().toString());
        d.put(InformeRival.DEF_DEF, spDefDef.getText().toString());
        d.put(InformeRival.DEF_MED, spDefMed.getText().toString());
        d.put(InformeRival.DEF_DEL, spDefDel.getText().toString());
        d.put(InformeRival.TRAN_DA, spTranDA.getText().toString());
        d.put(InformeRival.TRAN_AD, spTranAD.getText().toString());
        d.put(InformeRival.ABP_OFE, spAbpOfe.getText().toString());
        d.put(InformeRival.ABP_DEF, spAbpDef.getText().toString());
        d.put(InformeRival.FORTALEZAS, texto(etFortalezas));
        d.put(InformeRival.DEBILIDADES, texto(etDebilidades));
        d.put(InformeRival.OBS_OFE, texto(etObsOfe));
        d.put(InformeRival.OBS_DEF, texto(etObsDef));
        d.put(InformeRival.OBS_ABP, texto(etObsAbp));
        d.put(InformeRival.JUGADOR_PELIGROSO, texto(etJugadorPeligroso));
        d.put(InformeRival.ESLABON_DEBIL, texto(etEslabonDebil));
        return d;
    }

    public void pedirNombreRival() {
        if (getContext() == null) return;
        EditText etRival = new EditText(getContext());
        etRival.setHint("Nombre del rival (ej: Real Madrid)");
        etRival.setPadding(48, 32, 48, 32);
        new AlertDialog.Builder(getContext())
                .setTitle("Guardar Informe")
                .setMessage("Introduce el nombre del equipo rival:")
                .setView(etRival)
                .setPositiveButton("Guardar", (d, w) -> controlador.guardarInforme(etRival.getText().toString()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void mostrarListaInformes(String[] titulos) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Informes guardados")
                .setItems(titulos, (d, which) -> controlador.abrirInforme(which))
                .setNegativeButton("Cerrar", null)
                .show();
    }

    public void mostrarDetalleInforme(String titulo, String texto) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle(titulo)
                .setMessage(texto)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private String texto(TextInputEditText et) {
        if (et == null || et.getText() == null) return "";
        return et.getText().toString().trim();
    }
}
