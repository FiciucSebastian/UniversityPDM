import axios from 'axios';
import {authConfig, baseUrl, getLogger, withLogs} from '../core';
import {ComponentProps} from './ComponentProps';
import { Plugins } from "@capacitor/core";
const { Storage } = Plugins;

const componentUrl = `http://${baseUrl}/api/component`;

export const getComponents: (token: string) => Promise<ComponentProps[]> = token => {
    var result = axios.get(componentUrl, authConfig(token))
    result.then(async result => {
        for (const component of result.data) {
            await Storage.set({
                key: component._id!,
                value: JSON.stringify({
                    _id: component._id,
                    name: component.name,
                    quantity: component.quantity,
                    releaseDate: component.releaseDate,
                    inStock: component.inStock
                }),
            });
        }
    });
    return withLogs(result, 'getComponents');
}

export const createComponent: (token: string, component: ComponentProps) => Promise<ComponentProps[]> = (token, component) => {
    var result = axios.post(componentUrl, component, authConfig(token))
    result.then(async result => {
        var component = result.data;
        await Storage.set({
            key: component._id!,
            value: JSON.stringify({
                _id: component._id,
                name: component.name,
                quantity: component.quantity,
                releaseDate: component.releaseDate,
                inStock: component.inStock
            }),
        });
    });
    return withLogs(result, 'createComponent');
}

export const updateComponent: (token: string, component: ComponentProps) => Promise<ComponentProps[]> = (token, component) => {
    var result = axios.put(`${componentUrl}/${component._id}`, component, authConfig(token))
    result.then(async result => {
        var component = result.data;
        await Storage.set({
            key: component._id!,
            value: JSON.stringify({
                _id: component._id,
                name: component.name,
                quantity: component.quantity,
                releaseDate: component.releaseDate,
                inStock: component.inStock
            }),
        });
    });
    return withLogs(result, 'updateComponent');
}

export const removeComponent: (token: string, component: ComponentProps) => Promise<ComponentProps[]> = (token, component) => {
    var result = axios.delete(`${componentUrl}/${component._id}`, authConfig(token))
    result.then(async result => {
        await Storage.remove({key: component._id!})
    })
    return withLogs(result, 'deleteComponent');
}

interface MessageData {
    type: string;
    payload: ComponentProps;
}

const log = getLogger('ws');

export const newWebSocket = (token: string, onMessage: (data: MessageData) => void) => {
    const ws = new WebSocket(`ws://${baseUrl}`);
    ws.onopen = () => {
        log('web socket onopen');
        ws.send(JSON.stringify({type: 'authorization', payload: {token}}));
    };
    ws.onclose = () => {
        log('web socket onclose');
    };
    ws.onerror = error => {
        log('web socket onerror', error);
    };
    ws.onmessage = messageEvent => {
        log('web socket onmessage');
        onMessage(JSON.parse(messageEvent.data));
    };
    return () => {
        ws.close();
    }
}
