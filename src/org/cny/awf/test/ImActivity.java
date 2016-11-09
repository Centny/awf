package org.cny.awf.test;

import java.util.HashMap;
import java.util.Map;

import org.cny.jwf.im.IMC.MsgListener;
import org.cny.jwf.im.Msg;
import org.cny.jwf.im.PbSckIMC;
import org.cny.jwf.netw.r.Cmd;
import org.cny.jwf.netw.r.Netw;
import org.cny.jwf.netw.r.NetwRunnable;
import org.cny.jwf.netw.r.NetwRunnable.CmdListener;
import org.cny.jwf.netw.r.NetwRunnable.EvnListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ImActivity extends Activity implements EvnListener, MsgListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_im);
	}

	PbSckIMC imc;
	String host = "192.168.2.57";
	short port = 4001;

	public void onStart(View v) {
		this.imc = new PbSckIMC(this, this, this.host, this.port);
		new Thread(this.imc).start();
	}

	public void onStop(View v) {
		try {
			this.imc.close();
		} catch (Exception e) {

		}
	}

	@Override
	public void onMsg(Msg m) {
		System.out.println("-->" + new String(m.toString()));
	}

	@Override
	public void begCon(NetwRunnable nr) throws Exception {

	}

	@Override
	public void onCon(NetwRunnable nr, Netw nw) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("token", "58228ECED624D3448BD266A9");
		this.imc.li(args, new CmdListener() {

			@Override
			public void onCmd(NetwRunnable nr, Cmd m) {
				try {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("last", 1000);
					imc.ur(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void onErr(NetwRunnable nr, Throwable e) {
		e.printStackTrace();
	}
}
