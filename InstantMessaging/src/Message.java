import java.io.Serializable;
import java.util.Vector;

public class Message implements Serializable {
	private static final long serialVersionUID = -3831507106408529855L;	
	
	public String notice;//����
	public Vector userOnlineVector;//�û����߶���
	public Vector chatVector;//������Ϣ��
}