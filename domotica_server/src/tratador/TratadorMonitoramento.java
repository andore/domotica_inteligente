package tratador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import common.CodAtuador;
import common.EstMensagem;
import common.EstMonitora;
import common.EstruturaException;
import common.MensagemResp;
import common.Status;
import common.StructException;
import dao.Atuador;
import dao.AtuadorDao;
import dao.Cenario;
import dao.DbException;
import dao.Historico;
import dao.HistoricoDao;
import dao.Sensor;
import dao.SensorDao;
import main.ControleServer;

public class TratadorMonitoramento extends AbstractTratador 
{
	final static Logger logger = Logger.getLogger(TratadorMonitoramento.class);
	private EstMonitora resp = new EstMonitora();
	
	public TratadorMonitoramento()
	{
		
	}
	
	public EstMonitora processa(EstMensagem msg) throws StructException, DbException, EstruturaException
	{
		incluirHistorico(msg);
		atualizaSensores(msg);
		
		mantemIluminacao(ControleServer.getCenarioAtual());
		mantemTemperatura(ControleServer.getCenarioAtual(), msg);
		
		return resp;
	}
	
	private void atualizaSensores(EstMensagem msg) throws StructException, DbException, EstruturaException
	{
		EstMonitora vo = new EstMonitora(msg.getStrIn());
		
		for(Sensor sensor: vo.getSensores())
		{
			updateSensor(sensor);
		}
		
		for(Atuador atuador: vo.getAtuadores())
		{
			updateAtuador(atuador);
		}
	}
	
	private void incluirHistorico(EstMensagem msg) throws EstruturaException, DbException{
		EstMonitora mon = new EstMonitora(msg.getStrIn());		
		Historico hist;
		HistoricoDao histDao = new HistoricoDao();
		int idHist = histDao.getNextId();
		Date data = new Date();		
		
		for(Sensor sensor: mon.getSensores()){
			hist = new Historico();
			hist.setId_historico(idHist);
			hist.setValor_sensor(sensor.getValor());
			hist.setId_sensor(sensor.getId());
			hist.setId_arduino(mon.getIdArduino());			
			hist.setData_criacao(data);
			histDao.insere(hist);
		}
		
		for(Atuador atuador: mon.getAtuadores()){
			hist = new Historico();
			hist.setStatus_atuador(atuador.getStatus());
			hist.setId_atuador(atuador.getId());
			hist.setId_arduino(mon.getIdArduino());			
			hist.setId_historico(idHist);
			hist.setData_criacao(data);
			histDao.insere(hist);
		}
	}
	
	private void updateSensor(Sensor sensor) throws DbException
	{
		SensorDao dao = new SensorDao();
		Sensor sb = dao.serachById(sensor.getId());
		sb.setValor(sensor.getValor());
		dao.update(sb);
	}
	
	private void updateAtuador(Atuador atuador) throws DbException
	{
		AtuadorDao dao = new AtuadorDao();
		Atuador ab = dao.serachById(atuador.getId());
		ab.setStatus(atuador.getStatus());
		
		dao.update(ab);
	}
	
	private void mantemTemperatura(Cenario cenario, EstMensagem msg) throws EstruturaException, DbException
	{
		EstMonitora vo = new EstMonitora(msg.getStrIn());		
		
		for(Atuador a : vo.getAtuadores())
		{
			if(a.getCod() == CodAtuador.JANELA)
				a.setStatus(Status.ON_);
		}
		
		resp.setAtuadores(vo.getAtuadores());
		resp.setQtdAtuador(vo.getQtdAtuador());
	}
	
	private void mantemIluminacao(Cenario cenario) throws DbException, StructException
	{
		EstMonitora resposta = new EstMonitora();
		List<Atuador> atuadores = new ArrayList<Atuador>();
		AtuadorDao dao = new AtuadorDao();
		
		for(Atuador atuCen: cenario.getAtuadores())
		{
			Atuador atu = dao.serachById(atuCen.getId());
			
			if(atu.getStatus() != atuCen.getStatus())
			{
				atuadores.add(atuCen);
			}
		}
		resposta.setQtdSensor(0);
		resposta.setAtuadores(atuadores);
		resposta.setQtdAtuador(atuadores.size());
		resp = resposta;
	}

	@Override
	public MensagemResp processaHtml(EstMensagem msg) throws StructException, DbException, EstruturaException {
		// TODO Auto-generated method stub
		return null;
	}
}
