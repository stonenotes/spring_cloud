package com.stonenotes.lombok.slf4j;

import ch.qos.logback.core.PropertyDefinerBase;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: javan
 * @Date: 2019/6/26
 */
@Component
public class GetLocalIpProperty extends PropertyDefinerBase  {

    @Override
    public String getPropertyValue() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

}
