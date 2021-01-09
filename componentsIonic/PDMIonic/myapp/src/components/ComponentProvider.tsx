import React, {useCallback, useContext, useEffect, useReducer} from 'react';
import PropTypes from 'prop-types';
import {getLogger} from '../core';
import {createComponent, getComponents, newWebSocket, removeComponent, updateComponent} from './componentApi';
import {ComponentProps} from "./ComponentProps";
import {AuthContext} from "../auth";
import { Plugins } from "@capacitor/core";
const { Storage } = Plugins;

const log = getLogger('ComponentProvider');

type SaveComponentFn = (component: ComponentProps) => Promise<any>;
type DeleteComponentFn = (component: ComponentProps) => Promise<any>;

export interface ComponentsState {
    components?: ComponentProps[],
    fetching: boolean,
    fetchingError?: Error | null,
    saving: boolean,
    savingError?: Error | null,
    deleting: boolean,
    deletingError?: Error | null,
    saveComponent?: SaveComponentFn,
    deleteComponent?: DeleteComponentFn
}

interface ActionProps {
    type: string,
    payload?: any,
}

const initialState: ComponentsState = {
    fetching: false,
    saving: false,
    deleting: false
};

const FETCH_ITEMS_STARTED = 'FETCH_ITEMS_STARTED';
const FETCH_ITEMS_SUCCEEDED = 'FETCH_ITEMS_SUCCEEDED';
const FETCH_ITEMS_FAILED = 'FETCH_ITEMS_FAILED';
const SAVE_ITEM_STARTED = 'SAVE_ITEM_STARTED';
const SAVE_ITEM_SUCCEEDED = 'SAVE_ITEM_SUCCEEDED';
const SAVE_ITEM_FAILED = 'SAVE_ITEM_FAILED';
const DELETE_ITEM_STARTED = "DELETE_ITEM_STARTED";
const DELETE_ITEM_SUCCEEDED = "DELETE_ITEM_SUCCEEDED";
const DELETE_ITEM_FAILED = "DELETE_ITEM_FAILED";

const reducer: (state: ComponentsState, action: ActionProps) => ComponentsState =
    (state, {type, payload}) => {
        switch (type) {
            case FETCH_ITEMS_STARTED:
                return {...state, fetching: true, fetchingError: null};
            case FETCH_ITEMS_SUCCEEDED:
                return {...state, components: payload.components, fetching: false};
            case FETCH_ITEMS_FAILED:
                return {...state, components: payload.components, fetching: false};

            case SAVE_ITEM_STARTED:
                return {...state, savingError: null, saving: true};
            case SAVE_ITEM_SUCCEEDED:
                const components = [...(state.components || [])];
                const component = payload.component;
                const index = components.findIndex(it => it._id === component._id);
                if (index === -1) {
                    components.splice(0, 0, component);
                } else {
                    components[index] = component;
                }
                return {...state, components, saving: false};
            case SAVE_ITEM_FAILED:
                return {...state, components: payload.error, saving: false};

            case DELETE_ITEM_STARTED:
                return {...state, deletingError: null, deleting: true};
            case DELETE_ITEM_SUCCEEDED: {
                const components = [...(state.components || [])];
                const component = payload.component;
                const index = components.findIndex((it) => it._id === component._id);
                components.splice(index, 1);
                return {...state, components, deleting: false};
            }
            case DELETE_ITEM_FAILED:
                return {...state, deletingError: payload.error, deleting: false};
            default:
                return state;
        }
    };

export const ComponentContext = React.createContext<ComponentsState>(initialState);

interface ComponentProviderProps {
    children: PropTypes.ReactNodeLike,
}

export const ComponentProvider: React.FC<ComponentProviderProps> = ({children}) => {
    const {token} = useContext(AuthContext);
    const [state, dispatch] = useReducer(reducer, initialState);
    const {components, fetching, fetchingError, saving, savingError, deleting, deletingError} = state;
    useEffect(getComponentsEffect, [token]);
    useEffect(wsEffect, [token]);
    const saveComponent = useCallback<SaveComponentFn>(saveComponentCallback, [token]);
    const deleteComponent = useCallback<DeleteComponentFn>(deleteComponentCallback, [token]);
    const value = {components, fetching, fetchingError, saving, savingError, saveComponent, deleting, deletingError, deleteComponent};
    log('returns');
    return (
        <ComponentContext.Provider value={value}>
            {children}
        </ComponentContext.Provider>
    );

    function getComponentsEffect() {
        let canceled = false;
        fetchComponents();
        return () => {
            canceled = true;
        }

        async function fetchComponents() {
            let canceled = false;
            fetchComponents();
            return () => {
                canceled = true;
            }

            async function fetchComponents() {
                if (!token?.trim()) {
                    return;
                }
                try {
                    log('fetchComponents started');
                    dispatch({type: FETCH_ITEMS_STARTED});
                    const components = await getComponents(token);
                    log('fetchComponents succeeded');
                    if (!canceled) {
                        dispatch({type: FETCH_ITEMS_SUCCEEDED, payload: {components}});
                    }
                } catch (error) {

                    let storageKeys = Storage.keys();
                    const promisedComponents = await storageKeys.then(async function (storageKeys) {
                        const componentList = [];
                        for (let i = 0; i < storageKeys.keys.length; i++) {
                            // alert(storageKeys.keys[i])
                            if(storageKeys.keys[i] != 'token') {
                                const promisedComponent = await Storage.get({key: storageKeys.keys[i]});
                                // alert(promisedComponent.value)
                                if (promisedComponent.value != null) {
                                    var component = JSON.parse(promisedComponent.value);
                                }
                                componentList.push(component);
                            }
                        }
                        return componentList;
                    });

                    const components = promisedComponents
                    dispatch({type: FETCH_ITEMS_FAILED, payload: {components}});
                }
            }
        }
    }

    async function saveComponentCallback(component: ComponentProps) {
        try {
            log('saveComponent started');
            dispatch({type: SAVE_ITEM_STARTED});
            const savedComponent = await (component._id ? updateComponent(token, component) : createComponent(token, component));
            log('saveComponent succeeded');
            dispatch({type: SAVE_ITEM_SUCCEEDED, payload: {component: savedComponent}});
        } catch (error) {
            log('saveComponent failed');
            dispatch({type: SAVE_ITEM_FAILED, payload: {error}});
        }
    }

    async function deleteComponentCallback(component: ComponentProps) {
        try {
            log("delete started");
            dispatch({type: DELETE_ITEM_STARTED});
            const deletedComponent = await removeComponent(token, component);
            log("delete succeeded");
            console.log(deletedComponent);
            dispatch({type: DELETE_ITEM_SUCCEEDED, payload: {component: component}});
        } catch (error) {
            log("delete failed");
            dispatch({type: DELETE_ITEM_FAILED, payload: {error}});
        }
    }

    function wsEffect() {
        let canceled = false;
        log('wsEffect - connecting');
        let closeWebSocket: () => void;
        if (token?.trim()) {
            closeWebSocket = newWebSocket(token, message => {
                if (canceled) {
                    return;
                }
                const {type, payload: component} = message;
                log(`ws message, component ${type}`);
                if (type === 'created' || type === 'updated') {
                    dispatch({type: SAVE_ITEM_SUCCEEDED, payload: {component}});
                }
            });
        }
        return () => {
            log('wsEffect - disconnecting');
            canceled = true;
            closeWebSocket?.();
        }
    }
};