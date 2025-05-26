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
    public void refresh() {
        getDataSaldoAplikasi();
    }
        
    private void getDataSaldoAplikasi(){
        LocalTime[] jamShift = getWaktuShiftByNow();

        LocalDate tanggalHariIni = LocalDate.now();

        LocalDateTime dateTimeMulai = tanggalHariIni.atTime(jamShift[0]);
        LocalDateTime dateTimeSelesai = tanggalHariIni.atTime(jamShift[1]);

        Timestamp timestampMulai = Timestamp.valueOf(dateTimeMulai);
        Timestamp timestampSelesai = Timestamp.valueOf(dateTimeSelesai);
        
        try {
            String query = "WITH TransaksiShift AS (\n" +
            "    -- Menghitung total transaksi yang terjadi HANYA selama shift saat ini\n" +
            "    SELECT\n" +
            "        a.nama_aplikasi_saldo,\n" +
            "        a.total_saldo AS sisa_saldo_sekarang, -- Saldo terkini dari tabel aplikasi_saldo\n" +
            "        \n" +
            "        COALESCE(SUM(tsa.total_topup), 0) AS total_topup_shift,\n" +
            "        COALESCE(SUM(tsp.total_saldo_minus), 0) AS total_saldo_minus_shift,\n" +
            "        COALESCE(SUM(tsp.total_cashback), 0) AS total_cashback_shift\n" +
            "    FROM \n" +
            "        aplikasi_saldo a\n" +
            "    LEFT JOIN \n" +
            "        topup_saldo_aplikasi tsa ON a.id_aplikasi_saldo = tsa.id_aplikasi_saldo \n" +
            "                                 AND tsa.tanggal BETWEEN ? AND ? -- Filter waktu shift\n" +
            "    LEFT JOIN \n" +
            "        topup_saldo_pelanggan tsp ON a.id_aplikasi_saldo = tsp.id_aplikasi_saldo \n" +
            "                                  AND tsp.tanggal BETWEEN ? AND ? -- Filter waktu shift\n" +
            "    GROUP BY\n" +
            "        a.id_aplikasi_saldo, a.nama_aplikasi_saldo, a.total_saldo\n" +
            ")\n" +
            "SELECT \n" +
            "    T.nama_aplikasi_saldo,\n" +
            "    (T.sisa_saldo_sekarang - T.total_topup_shift + T.total_saldo_minus_shift + T.total_cashback_shift) AS saldo_awal,\n" +
            "    T.total_saldo_minus_shift AS minus_saldo,\n" +
            "    T.total_topup_shift AS topup,\n" +
            "    T.total_cashback_shift AS cashback,\n" +
            "    T.sisa_saldo_sekarang AS sisa_saldo\n" +
            "FROM \n" +
            "    TransaksiShift T;";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setTimestamp(1, timestampMulai);
            statement.setTimestamp(2, timestampSelesai);
            statement.setTimestamp(3, timestampMulai);
            statement.setTimestamp(4, timestampSelesai);
            
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
    
    public LocalTime[] getWaktuShiftByNow() {
        LocalTime[] waktuShift = new LocalTime[2];
        LocalTime now = LocalTime.now();

        LocalTime pagiMulai = LocalTime.parse("07:30:00");
        LocalTime pagiSelesai = LocalTime.parse("15:30:00");
        LocalTime malamMulai = LocalTime.parse("15:30:01");
        LocalTime malamSelesai = LocalTime.parse("23:30:00");

        if (!now.isBefore(pagiMulai) && !now.isAfter(pagiSelesai)) {
            // shift pagi
            waktuShift[0] = pagiMulai;
            waktuShift[1] = pagiSelesai;
        } else if (!now.isBefore(malamMulai) && !now.isAfter(malamSelesai)) {
            // shift malam
            waktuShift[0] = malamMulai;
            waktuShift[1] = malamSelesai;
        } else {
            // di luar jam shift, bisa return null atau default
            waktuShift[0] = null;
            waktuShift[1] = null;
        }
        return waktuShift;
    }
}
