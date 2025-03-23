package karyawan.halamanSaldo;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.Koneksi;
import main.Session;

public class HalamanSaldoKController implements Initializable {

    @FXML Label lblSaldoAwalPayfazz, lblSaldoAwalDigipos, lblSaldoAwalDompul, lblSaldoAwalMobo, lblSaldoAwalRita;
    @FXML Label lblSaldoMinusPayfazz, lblSaldoMinusDigipos, lblSaldoMinusDompul, lblSaldoMinusMobo, lblSaldoMinusRita;
    @FXML Label lblSaldoTopupPayfazz, lblSaldoTopupDigipos, lblSaldoTopupDompul, lblSaldoTopupMobo, lblSaldoTopupRita;
    @FXML Label lblSaldoCashbackPayfazz, lblSaldoCashbackDigipos, lblSaldoCashbackDompul, lblSaldoCashbackMobo, lblSaldoCashbackRita;
    @FXML Label lblSaldoAkhirPayfazz, lblSaldoAkhirDigipos, lblSaldoAkhirDompul, lblSaldoAkhirMobo, lblSaldoAkhirRita;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setSaldoAplikasi();
    }    
    
    private void setSaldoAplikasi(){
        try {
            String query = "SELECT aps.nama_aplikasi_saldo AS nama_app, ds.total_saldo_awal AS saldo_awal,\n" +
            "ds.total_minus_saldo AS total_minus, ds.total_topup AS total_topup, ds.total_cashback AS total_cashback, \n"
            + "ds.total_saldo_akhir AS saldo_akhir \n" +
            "FROM detail_saldo ds\n" +
            "JOIN aplikasi_saldo aps ON ds.id_jenis_aplikasi_saldo = aps.id_jenis_aplikasi_saldo\n" +
            "ORDER BY ds.id_detail_saldo DESC LIMIT 5";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String saldoAwal = Session.convertIntToRupiah(result.getInt("saldo_awal"));
                String totalMinus = Session.convertIntToRupiah(result.getInt("total_minus"));
                String totalTopup = Session.convertIntToRupiah(result.getInt("total_topup"));
                String totalCashback = Session.convertIntToRupiah(result.getInt("total_cashback"));
                String saldoAkhir = Session.convertIntToRupiah(result.getInt("saldo_akhir"));
                
                switch(result.getString("nama_app")){
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
