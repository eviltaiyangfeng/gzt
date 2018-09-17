package com.gzt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.gzt.utils.ClutterUtils;
import com.gzt.utils.ExeCommand;
import java.util.ArrayList;
import java.util.List;

public class MyAbService extends AccessibilityService {
    public static AccessibilityNodeInfo sRootNode;
    public static String sPackageName;
    public static String sActivityName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        sRootNode = getRootInActiveWindow();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            sPackageName = event.getPackageName().toString();
            sActivityName = event.getClassName().toString();
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                stopAccessibilityService();
                System.exit(0);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                break;
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopAccessibilityService();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    public static String findNoteById(Context context, String id) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return "";
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo.getText().toString();
                }
            }
        }
        return "";
    }

    public static String findNoteById_noThrows(Context context, String id) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return "";
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo.getText().toString();
                }
            }
        }
        return "";
    }

    public static Boolean clickById(Context context, String id) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getViewIdResourceName() != null && nodeInfo.getViewIdResourceName().equals(id)) {
                    while (nodeInfo != null) {
                        if (nodeInfo.isClickable()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        nodeInfo = nodeInfo.getParent();
                    }
                }
            }
        }
        return false;
    }

    public static Boolean clickById_noThorws(Context context, String id) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getViewIdResourceName() != null && nodeInfo.getViewIdResourceName().equals(id)) {
                    while (nodeInfo != null) {
                        if (nodeInfo.isClickable()) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        nodeInfo = nodeInfo.getParent();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取所有子节点
     *
     * @param myrootNode
     * @return
     */
    public static List<AccessibilityNodeInfo> getNodes(Context context, AccessibilityNodeInfo myrootNode) {
        throws_exception();
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodes(context, child);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    nodes.add(child);
                }
            }
        }
        return nodes;
    }

    public static List<AccessibilityNodeInfo> getNodes_noThrows(Context context, AccessibilityNodeInfo myrootNode) {
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodes_noThrows(context, child);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    nodes.add(child);
                }
            }
        }
        return nodes;
    }

    /**
     * 获取所有与id匹配的子节点
     *
     * @param context    context
     * @param myrootNode 根节点
     * @param id         id
     * @return 匹配到的节点集合
     */
    public static List<AccessibilityNodeInfo> getNodesById(Context context, AccessibilityNodeInfo myrootNode, String id) {
        throws_exception();
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodesById(context, child, id);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    if (child.toString().contains(id)) {
                        nodes.add(child);
                    }
                }
            }
        }
        return nodes;
    }

    public static List<AccessibilityNodeInfo> getNodesById_noThrows(Context context, AccessibilityNodeInfo myrootNode, String id) {
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodesById_noThrows(context, child, id);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    if (child.toString().contains(id)) {
                        nodes.add(child);
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * 获取所有与text匹配的子节点
     *
     * @param context    context
     * @param myrootNode 根节点
     * @param text       text
     * @return 匹配到的节点集合
     */
    public static List<AccessibilityNodeInfo> getNodesByText(Context context, AccessibilityNodeInfo myrootNode, String text) {
        throws_exception();
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodesByText(context, child, text);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    if (child.getText() != null && child.getText().toString().equals(text)) {
                        nodes.add(child);
                    }
                }
            }
        }
        return nodes;
    }

    public static List<AccessibilityNodeInfo> getNodesByText_noThrows(Context context, AccessibilityNodeInfo myrootNode, String text) {
        checkAsbStatus(context);
        if (myrootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = new ArrayList();
        int sum = myrootNode.getChildCount();
        if (sum > 0) {
            for (int i = 0; i < sum; i++) {
                AccessibilityNodeInfo child = myrootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 0) {
                    List<AccessibilityNodeInfo> res = getNodesByText_noThrows(context, child, text);
                    if (res != null) {
                        nodes.addAll(res);
                    }
                } else {
                    if (child.getText() != null && child.getText().toString().equals(text)) {
                        nodes.add(child);
                    }
                }
            }
        }
        return nodes;
    }


    public static Boolean inputNextNodeByText(Context context, String text, String inputStr) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList == null) return false;
        for (int i = 0; i < nodeList.size(); i++) {
            CharSequence gt = nodeList.get(i).getText();
            if (gt != null && text.equals(gt.toString())) {
                nodeList.get(i + 1).performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                new ExeCommand().run("input text " + inputStr, 10000).getResult();
                Thread.sleep(500);
                return true;
            }
        }
        return false;
    }

    public static String getNextTextByText(Context context, String text) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList == null) return "";
        for (int i = 0; i < nodeList.size(); i++) {
            CharSequence gt = nodeList.get(i).getText();
            if (gt != null && text.equals(gt.toString())) {
                AccessibilityNodeInfo nextNode = nodeList.get(i + 1);
                if (nextNode != null && nextNode.getText() != null)
                    return nextNode.getText().toString();
            }
        }
        return "";
    }

    public static String getTextByTextContains(Context context, String text) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList == null) return "";
        for (AccessibilityNodeInfo node : nodeList) {
            if (node != null && node.getText() != null) {
                if (node.getText().toString().contains(text)) return node.getText().toString();
            }
        }
        return "";
    }

    /**
     * 根据contentDescription查找子节点
     *
     * @return
     */
    public static Boolean findByDesc(Context context, String contentDescription) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodelist = getNodes(context, myrootNode);
        if (nodelist != null) {
            for (AccessibilityNodeInfo node : nodelist) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().equals(contentDescription)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean findByDesc_noThrows(Context context, String contentDescription) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().equals(contentDescription)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean findByDescContains_noThrows(Context context, String contentDescription) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().contains(contentDescription)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据contentDescription查找点击节点
     *
     * @param context
     * @param contentDescription
     * @return
     */
    public static Boolean clickByDesc(Context context, String contentDescription) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().equals(contentDescription)) {
                    while (node != null) {
                        if (node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        node = node.getParent();
                    }
                }
            }
        }
        return false;
    }

    public static Boolean clickByDesc_noThrows(Context context, String contentDescription) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().equals(contentDescription)) {
                    while (node != null) {
                        if (node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        node = node.getParent();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据contentDescription查找点击节点
     *
     * @param context
     * @param contentDescription
     * @return
     */
    public static Boolean clickByDescContains(Context context, String contentDescription) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().contains(contentDescription)) {
                    while (node != null) {
                        if (node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        node = node.getParent();
                    }
                }
            }
        }
        return false;
    }

    public static Boolean clickByDescContains_noThrows(Context context, String contentDescription) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getContentDescription() != null && node.getContentDescription().toString().contains(contentDescription)) {
                    while (node != null) {
                        if (node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        node = node.getParent();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据text查找子节点
     *
     * @param context
     * @param text
     * @return
     */
    public static Boolean findByText(Context context, String text) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().equals(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean findByText_noThrows(Context context, String text) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().equals(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据text查找子节点
     *
     * @param context
     * @param text
     * @return
     */
    public static Boolean findByTextContains(Context context, String text) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean findByTextContains_noThrows(Context context, String text) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据text查找点击节点
     *
     * @param context    context
     * @param text       text值
     * @return 成功返回true 失败返回false
     */
    public static Boolean clickByText(Context context, String text) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().equals(text)) {
                        while (node != null) {
                            if (node.isClickable()) {
                                int left = 0,right= 0,top=0,bottom=0;
                                Rect rect = new Rect(left,right,top,bottom);
                                node.getBoundsInScreen(rect);
                                int tapX = rect.left+(rect.right-rect.left)/2;
                                int tapY = rect.top+(rect.bottom-rect.top)/2;
                                ClutterUtils.tap(tapX,tapY);
                                return true;
                            }
                            node = node.getParent();
                        }
                }
            }
        }
        return false;
    }

    public static Boolean clickByText_noThrows(Context context, String text) {
        checkAsbStatus(context);
        AccessibilityNodeInfo myrootNode = MyAbService.sRootNode;
        if (myrootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, myrootNode);
        if (nodeList != null) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().equals(text)) {
                    while (node != null) {
                        if (node.isClickable()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        node = node.getParent();
                    }
                }
            }
        }
        return false;
    }

    public static Boolean clickByTextContains(Context context, String text) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getText() != null) {
                    if (nodeInfo.getText().toString().contains(text)) {
                        while (nodeInfo != null) {
                            if (nodeInfo.isClickable()) {
                                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                return true;
                            }
                            nodeInfo = nodeInfo.getParent();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean clickByTextContains_noThrows(Context context, String text) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getText() != null) {
                    if (nodeInfo.getText().toString().contains(text)) {
                        while (nodeInfo != null) {
                            if (nodeInfo.isClickable()) {
                                int left = 0,right= 0,top=0,bottom=0;
                                Rect rect = new Rect(left,right,top,bottom);
                                nodeInfo.getBoundsInScreen(rect);
                                int tapX = ClutterUtils.getRandomInt(rect.left+37,rect.left+300);
                                int tapY = ClutterUtils.getRandomInt(rect.top+36,rect.top+80);
                                ClutterUtils.tap(tapX,tapY);
                                return true;
                            }
                            nodeInfo = nodeInfo.getParent();
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean clickChatLink(Context context, String linkStr) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByText(linkStr);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            ArrayList<AccessibilityNodeInfo> resList = new ArrayList<>();
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getText() != null) {
                    if (nodeInfo.getText().toString().contains(linkStr)) {
                        resList.add(nodeInfo);
                    }
                }
            }
            if (resList.size() > 0) {
                AccessibilityNodeInfo nodeInfo = resList.get(resList.size()-1);
                while (nodeInfo != null) {
                    if (nodeInfo.isClickable()) {
                        int left = 0,right= 0,top=0,bottom=0;
                        Rect rect = new Rect(left,right,top,bottom);
                        nodeInfo.getBoundsInScreen(rect);
                        int tapX = ClutterUtils.getRandomInt(rect.left+66,rect.left+300);
                        int tapY = ClutterUtils.getRandomInt(rect.top+40,rect.top+70);
                        ClutterUtils.tap(tapX,tapY);
                        return true;
                    }
                    nodeInfo = nodeInfo.getParent();
                }
            }
        }
        return false;
    }

    public static Boolean selectCheckBoxById(Context context, String id) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }


    public static Boolean selectCheckBoxById_noThrows(Context context, String id) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据id找到input输入字符
     *
     * @param context      context
     * @param id           viewid
     * @param text         要输入的字符串
     * @return 成功返回true 失败返回false
     */
    public static Boolean inputTextById(Context context, String id, String text) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    new ExeCommand().run("input text " + text, 10000).getResult();
                    Thread.sleep(500);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据控件类名如android.widget.EditText在当前页面查找并返回第一个此类型控件的viewId
     * @param context
     * @param className
     * @return
     */
    public static String getFirstViewIdResourceName(Context context,String className) {
        List<AccessibilityNodeInfo> nodeInfoList =  getNodes(context,MyAbService.sRootNode);
        if (nodeInfoList!=null&&!nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo: nodeInfoList) {
                if (nodeInfo != null) {
                    if (className.equals(nodeInfo.getClassName())) {
                        return nodeInfo.getViewIdResourceName();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据控件类名如android.widget.EditText在当前页面查找到最后一个此类型控件的viewId点击
     * @param context
     * @param className
     * @return
     */
    public static Boolean clickLastViewIdByClassName(Context context,String className) {
        List<AccessibilityNodeInfo> nodeInfoList =  getAllNodeInfoByClassName(context, className);
        if (nodeInfoList.size() > 0) {
            AccessibilityNodeInfo nodeInfo = nodeInfoList.get(nodeInfoList.size()-1);
            if (nodeInfo.isClickable()) {
                int left = 0,right= 0,top=0,bottom=0;
                Rect rect = new Rect(left,right,top,bottom);
                nodeInfo.getBoundsInScreen(rect);
                int tapX = ClutterUtils.getRandomInt(rect.left+66,rect.left+300);
                int tapY = ClutterUtils.getRandomInt(rect.top+40,rect.top+70);
                ClutterUtils.tap(tapX,tapY);
                return true;
            }
        }
        return false;
    }

    /**
     * 根据控件类名如android.widget.EditText在当前页面查找并返回所有此类型控件节点信息
     * @param context
     * @param className
     * @return
     */
    public static List<AccessibilityNodeInfo> getAllNodeInfoByClassName(Context context,String className) {
        List<AccessibilityNodeInfo> nodeInfoList = getNodes(context,MyAbService.sRootNode);
        List<AccessibilityNodeInfo> viewIds = new ArrayList <>();
        if (nodeInfoList!=null&&!nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo: nodeInfoList) {
                if (nodeInfo != null) {
                    if (className.equals(nodeInfo.getClassName())) {
                        String viewId = nodeInfo.getViewIdResourceName();
                        if (!viewId.isEmpty()) {
                            viewIds.add(nodeInfo);
                        }
                    }
                }
            }
        }
        return viewIds;
    }

    public static Boolean paste(Context context, String id) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    Thread.sleep(1000);
                    //粘贴进入内容
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    Thread.sleep(500);
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean inputTextById_noThrows(Context context, String id, String text) throws Exception {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    new ExeCommand().run("input text " + text, 10000).getResult();
                    Thread.sleep(500);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回完整的text
     * @param context
     * @param text
     * @return
     */
    public static String getCompleteText(Context context, String text) {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return "";
        }
        List<AccessibilityNodeInfo> nodeList = getNodes(context, rootnodeinfo);
        if (nodeList != null && !nodeList.isEmpty()) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().contains(text)) {
                    return node.getText().toString();
                }
            }
        }
        return "";
    }

    public static String getCompleteText_noThrows(Context context, String text) {
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return "";
        }
        List<AccessibilityNodeInfo> nodeList = getNodes_noThrows(context, rootnodeinfo);
        if (nodeList != null && !nodeList.isEmpty()) {
            for (AccessibilityNodeInfo node : nodeList) {
                if (node != null && node.getText() != null && node.getText().toString().contains(text)) {
                    return node.getText().toString();
                }
            }
        }
        return "";
    }

    /**
     * 获得节点作用域右下角坐标
     *
     * @param context
     * @param text
     * @return
     * @throws Exception
     */
    public static Rect getRectbyText(Context context, String text) throws Exception {
        throws_exception();
        checkAsbStatus(context);
        AccessibilityNodeInfo rootnodeinfo = MyAbService.sRootNode;
        if (rootnodeinfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootnodeinfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.getText() != null) {
                    if (nodeInfo.getText().toString().equals(text)) {
                        int left = 0, top = 0, right = 0, bottom = 0;
                        Rect rect = new Rect(left, top, right, bottom);
                        nodeInfo.getBoundsInScreen(rect);
                        return rect;
                    }
                }
            }
        }
        return null;
    }


    public static void stopAccessibilityService() {
        //put一个不存在的，就会关闭已经开启的
        new ExeCommand().run("settings  put  secure  enabled_accessibility_services com.gzt/com.gzt.QHB", 10000).getResult();
    }

    public static void startAccessibilityService() {
        new ExeCommand().run("settings  put  secure  enabled_accessibility_services com.gzt/com.gzt.MyAbService", 10000).getResult();
        new ExeCommand().run("settings  put  secure  accessibility_enabled  1", 10000).getResult();
    }

    /**
     * 检查AccessibilityService状态，如果关闭了就开启
     *
     * @param context
     */
    private static void checkAsbStatus(Context context) {
        if (!ClutterUtils.isAccessibilitySettingsOn(context)) {
            startAccessibilityService();
        }
    }

    /**
     * 自定义异常
     */
    public static void throws_exception() {

    }

}
