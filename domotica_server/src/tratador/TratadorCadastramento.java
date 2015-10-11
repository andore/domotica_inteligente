package tratador;

import org.apache.log4j.Logger;

import common.Mensagem;
import common.StrCadastraArduino;
import common.StructException;
import dao.AbstractDao;
import dao.Arduino;
import dao.ArduinoDao;
import dao.DbException;
import hbn.ControleHbn;

public class TratadorCadastramento 
{
	private ControleHbn db;
	final static Logger logger = Logger.getLogger(TratadorCadastramento.class);
	public TratadorCadastramento()
	{
		db  = new ControleHbn();
	}
	
	public String processa(Mensagem msg) throws StructException, DbException
	{
		if(msg==null)
		{
			return null;
		}
		
		StrCadastraArduino cadastra = new StrCadastraArduino(msg.getMensagem());
		Arduino arduino = new Arduino();
		ArduinoDao dao = new ArduinoDao(db.getSession());
		
		if(cadastra!=null)
		{
			logger.debug("Cadastrando Arduino:[" + cadastra.getDescricaoArduino().trim() + "]");

			arduino.setId(msg.getIdArduino());
			arduino.setIp(msg.getIp());
			arduino.setDescricao(cadastra.getDescricaoArduino());
			arduino.setQtdSensor(cadastra.getQtdSensor());
			arduino.setSensores(cadastra.getSensores());
			arduino.setQtdAtuador(cadastra.getQtdAtuador());
			arduino.setAtuadores(cadastra.getAtuadores());
			
			dao.insereArduino(arduino);	
			logger.debug("Cadastrado com Sucesso");
		}
		
		return"ok";
	}
	
}
