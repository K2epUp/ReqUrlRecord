package com.self.burp;

import burp.*;
import com.self.burp.ui.BurpUIMain;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.List;

public class BurpExtender implements IBurpExtender, ITab, IHttpListener {
    public static boolean pluginState = false;
    public static String filteHost = "";
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private JPanel jPanelMain;
    // 创建BurpUIMain对象才能实现对UI内容修改
    BurpUIMain burpUIMain = new BurpUIMain();

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks iBurpExtenderCallbacks) {
        this.callbacks = iBurpExtenderCallbacks;
        // 设置插件名字
        callbacks.setExtensionName("RequestUrlPrint");
        this.helpers = callbacks.getHelpers();
        // 激活插件时,显示的内容
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stdout.println("Colect Request URL,Or fileter request for Host.");
        this.stdout.println("Loading successfully ...");
        // 注册监听器
        callbacks.registerHttpListener(this);
        // 初始化UI组件,获取到 UI 中主界面变量,注册到 Burp
        jPanelMain = burpUIMain.getRoot();
        callbacks.customizeUiComponent(jPanelMain);
        // 添加自定义的Tab页面.
        callbacks.addSuiteTab(this);
    }

    @Override
    public void processHttpMessage(int i, boolean b, IHttpRequestResponse iHttpRequestResponse) {
        if (pluginState) {
            if (i==callbacks.TOOL_PROXY || i==callbacks.TOOL_REPEATER || i==callbacks.TOOL_INTRUDER) {
                if (b) {
                    IRequestInfo iRequestInfo = helpers.analyzeRequest(iHttpRequestResponse);
                    String reqUrl = iRequestInfo.getUrl().toString();
                    if (filteHost.length()==0) {
                        if (!burpUIMain.getTextArea().getText().contains(reqUrl)) {
                            burpUIMain.getTextArea().append(iRequestInfo.getMethod() + "请求 -->> " + reqUrl);
                            burpUIMain.getTextArea().append("\n");
                        }
                    } else {
                        if(reqUrl.contains(filteHost)){
                            if (!burpUIMain.getTextArea().getText().contains(reqUrl)) {
                                burpUIMain.getTextArea().append(reqUrl);
                                burpUIMain.getTextArea().append("\n");
                            }
                        }
                    }

//                List<IParameter> parameters = iRequestInfo.getParameters();
                    String request = new String(iHttpRequestResponse.getRequest());
                    List<String> headers = iRequestInfo.getHeaders();
                    byte[] body = request.substring(iRequestInfo.getBodyOffset()).getBytes();
                    headers.add("reqtest:abcd1234");
                    byte[] newreq = helpers.buildHttpMessage(headers, body);
                    iHttpRequestResponse.setRequest(newreq);
                } else {
                        IResponseInfo iResponseInfo = helpers.analyzeResponse(iHttpRequestResponse.getResponse());
                        List<String> headers = iResponseInfo.getHeaders();
                        String resp = new String(iHttpRequestResponse.getResponse());
                        byte[] body = resp.substring(iResponseInfo.getBodyOffset()).getBytes();
                        headers.add("resp1:resp1111111");
                        byte[] newresp = helpers.buildHttpMessage(headers, body);
                    iHttpRequestResponse.setResponse(newresp);
                }
            }
            stdout.println(
                    (b ? "HTTP request to " : "HTTP response from ") + iHttpRequestResponse.getHttpService() + "[" + callbacks.getToolName(i) + "]"
            );

//        this.jTextArea.append((b ? "HTTP request to ":"HTTP response from ")+iHttpRequestResponse.getHttpService()+"["+callbacks.getToolName(i)+"]");
//        this.jTextArea.append("\n");
        }

    }

    @Override
    public String getTabCaption() {
        // 设置自定义Tab名称
        return "ReqUrlRecord";
    }

    @Override
    public Component getUiComponent() {
        return jPanelMain;
    }

}
