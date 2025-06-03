package karyawan.halamanSaldo;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.Koneksi;
import main.Pelengkap;
import main.Session;

public class HalamanSaldoKController implements Initializable, Pelengkap {

    @FXML Label lblSaldoAwalPayfazz, lblSaldoAwalDigipos, lblSaldoAwalDompul, lblSaldoAwalMobo, lblSaldoAwalRita;
    @FXML Label lblSaldoMinusPayfazz, lblSaldoMinusDigipos, lblSaldoMinusDompul, lblSaldoMinusMobo, lblSaldoMinusRita;
    @FXML Label lblSaldoTopupPayfazz, lblSaldoTopupDigipos, lblSaldoTopupDompul, lblSaldoTopupMobo, lblSaldoTopupRita;
    @FXML Label lblSaldoCashbackPayfazz, lblSaldoCashbackDigipos, lblSaldoCashbackDompul, lblSaldoCashbackMobo, lblSaldoCashbackRita;
    @FXML Label lblSaldoAkhirPayfazz, lblSaldoAkhirDigipos, lblSaldoAkhirDompul, lblSaldoAkhirMobo, lblSaldoAkhirRita;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getDataSaldoAplikasi();
    }    

    @Override
    public void perbarui() {
        getDataSaldoAplikasi();
    }
        
    private void getDataSaldoAplikasi(){
        LocalTime[] jamShift = Session.getWaktuShiftByNow();

        LocalDate tanggalHariIni = LocalDate.now();

        LocalDateTime dateTimeMulai = tanggalHariIni.atTime(jamShift[0]);
        LocalDateTime dateTimeSelesai = tanggalHariIni.atTime(jamShift[1]);

        Timestamp timestampMulai = Timestamp.valueOf(dateTimeMulai);
        Timestamp timestampSelesai = Timestamp.valueOf(dateTimeSelesai);
        
        try {
            String query = "SELECT \n" +
            "    aps.nama_aplikasi_saldo,\n" +
            "\n" +
            "    -- saldo awal: ambil dari log terakhir\n" +
            "    COALESCE((\n" +
            "        SELECT lsa1.saldo_akhir_shift\n" +
            "        FROM log_saldo_awal lsa1\n" +
            "        WHERE lsa1.id_aplikasi_saldo = aps.id_aplikasi_saldo\n" +
            "        ORDER BY lsa1.waktu_shift_selesai DESC\n" +
            "        LIMIT 1\n" +
            "    ), 0) AS saldo_awal,\n" +
            "\n" +
            "    -- total minus pelanggan dalam shift\n" +
            "    COALESCE((\n" +
            "        SELECT SUM(tsp.total_saldo_minus)\n" +
            "        FROM topup_saldo_pelanggan tsp\n" +
            "        WHERE tsp.id_aplikasi_saldo = aps.id_aplikasi_saldo\n" +
            "          AND tsp.tanggal BETWEEN ? AND ?\n" +
            "    ), 0) AS minus_saldo,\n" +
            "\n" +
            "    -- total topup admin dalam shift\n" +
            "    COALESCE((\n" +
            "        SELECT SUM(tsa.total_topup)\n" +
            "        FROM topup_saldo_aplikasi tsa\n" +
            "        WHERE tsa.id_aplikasi_saldo = aps.id_aplikasi_saldo\n" +
            "          AND tsa.tanggal BETWEEN ? AND ?\n" +
            "    ), 0) AS topup,\n" +
            "\n" +
            "    -- total cashback ke pelanggan dalam shift\n" +
            "    COALESCE((\n" +
            "        SELECT SUM(tsp.total_cashback)\n" +
            "        FROM topup_saldo_pelanggan tsp\n" +
            "        WHERE tsp.id_aplikasi_saldo = aps.id_aplikasi_saldo\n" +
            "          AND tsp.tanggal BETWEEN ? AND ?\n" +
            "    ), 0) AS cashback,\n" +
            "\n" +
            "    -- saldo akhir sekarang (real-time)\n" +
            "    aps.total_saldo AS sisa_saldo\n" +
            "\n" +
            "FROM aplikasi_saldo aps;";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setTimestamp(1, timestampMulai);
            statement.setTimestamp(2, timestampSelesai);
            statement.setTimestamp(3, timestampMulai);
            statement.setTimestamp(4, timestampSelesai);
            statement.setTimestamp(5, timestampMulai);
            statement.setTimestamp(6, timestampSelesai);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String saldoAwal = Session.convertIntToRupiah(result.getInt("saldo_awal"));
                String totalMinus = Session.convertIntToRupiah(result.getInt("minus_saldo"));
                String totalTopup = Session.convertIntToRupiah(result.getInt("topup"));
                String totalCashback = Session.convertIntToRupiah(result.getInt("cashback"));
                String saldoAkhir = Session.convertIntToRupiah(result.getInt("sisa_saldo"));
                
                switch(result.getString("nama_aplikasi_saldo")){
                    case "Payfazz":
                        lblSaldoAwalPayfazz.setText(saldoAwal);
                        lblSaldoMinusPayfazz.setText(totalMinus);
                        lblSaldoTopupPayfazz.setText(totalTopup);
                        lblSaldoCashbackPayfazz.setText(totalCashback);
                        lblSaldoAkhirPayfazz.setText(saldoAkhir);
                        break;
                    case "Digipos":
                        lblSaldoAwalDigipos.setText(saldoAwal);
                        lblSaldoMinusDigipos.setText(totalMinus);
                        lblSaldoTopupDigipos.setText(totalTopup);
                        lblSaldoCashbackDigipos.setText(totalCashback);
                        lblSaldoAkhirDigipos.setText(saldoAkhir);
                        break;
                    case "Dompul":
                        lblSaldoAwalDompul.setText(saldoAwal);
                        lblSaldoMinusDompul.setText(totalMinus);
                        lblSaldoTopupDompul.setText(totalTopup);
                        lblSaldoCashbackDompul.setText(totalCashback);
                        lblSaldoAkhirDompul.setText(saldoAkhir);
                        break;
                    case "Mobo":
                        lblSaldoAwalMobo.setText(saldoAwal);
                        lblSaldoMinusMobo.setText(totalMinus);
                        lblSaldoTopupMobo.setText(totalTopup);
                        lblSaldoCashbackMobo.setText(totalCashback);
                        lblSaldoAkhirMobo.setText(saldoAkhir);
                        break;
                    case "Rita":
                        lblSaldoAwalRita.setText(saldoAwal);
                        lblSaldoMinusRita.setText(totalMinus);
                        lblSaldoTopupRita.setText(totalTopup);
                        lblSaldoCashbackRita.setText(totalCashback);
                        lblSaldoAkhirRita.setText(saldoAkhir);
                        break;
                }
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
