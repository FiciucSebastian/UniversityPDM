import React, {useContext, useEffect, useState} from 'react';
import {RouteComponentProps} from 'react-router';
import {
    IonContent,
    IonFab,
    IonFabButton,
    IonHeader,
    IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonLabel,
    IonList, IonListHeader, IonLoading,
    IonPage,
    IonTitle,
    IonToolbar
} from '@ionic/react';
import {add} from 'ionicons/icons';
import {getLogger} from '../core';
import {ComponentContext} from "./ComponentProvider";
import Component from "./Component";
import {AuthContext} from "../auth";
import {ComponentProps} from "./ComponentProps";

const log = getLogger('ComponentList');

const offset = 20;

const ComponentList: React.FC<RouteComponentProps> = ({history}) => {
    const {logout} = useContext(AuthContext);
    const {components, fetching, fetchingError} = useContext(ComponentContext);
    const [disableInfiniteScroll, setDisableInfiniteScroll] = useState(false);
    const [visibleComponents, setVisibleComponents] = useState<ComponentProps[] | undefined>([]);
    const [page, setPage] = useState(offset)

    useEffect(()=>{
        setPage(offset)
        fetchData();
    }, [components]);

    function fetchData(){
        setVisibleComponents(components?.slice(0, page))
        setPage(page + offset);
        if (components && page > components?.length) {
            setDisableInfiniteScroll(true);
            setPage(components.length);
        }
        else {
            setDisableInfiniteScroll(false);
        }
    }

    async function getNextPage($event:CustomEvent<void>){
        fetchData();
        ($event.target as HTMLIonInfiniteScrollElement).complete();
    }

    log('render');
    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Components</IonTitle>
                </IonToolbar>
            </IonHeader>
            <IonContent>
                <IonLoading isOpen={fetching} message="Fetching components"/>
                {visibleComponents && (
                    <IonList>
                        <IonListHeader lines="inset">
                            <IonLabel>Name</IonLabel>
                            <IonLabel>Nr of quantity</IonLabel>
                            <IonLabel>Date</IonLabel>
                            <IonLabel>inStock?</IonLabel>
                        </IonListHeader>
                        {visibleComponents.map(({_id, name, quantity, releaseDate, inStock}) =>
                            <Component key={_id} _id={_id} name={name} quantity={quantity}
                                             releaseDate={releaseDate} inStock={inStock}
                                             onEdit={_id => history.push(`/component/${_id}`)}/>
                        )}
                    </IonList>
                )}
                <IonInfiniteScroll threshold = "100px" disabled={disableInfiniteScroll}
                                   onIonInfinite = {(e:CustomEvent<void>)=>getNextPage(e)}>
                    <IonInfiniteScrollContent
                        loadingText="Loading more components...">
                    </IonInfiniteScrollContent>
                </IonInfiniteScroll>
                {fetchingError && (
                    <div>{fetchingError.message || 'Failed to fetch components'}</div>
                )}
                <IonFab vertical="bottom" horizontal="end" slot="fixed">
                    <IonFabButton onClick={() => history.push('/component')}>
                        <IonIcon icon={add}/>
                    </IonFabButton>
                </IonFab>
                <IonFab vertical="bottom" horizontal="start" slot="fixed">
                    <IonFabButton onClick={handleLogout}>
                        Logout
                    </IonFabButton>
                </IonFab>
            </IonContent>
        </IonPage>
    );

    function handleLogout() {
        log("logout");
        logout?.();
    }
};

export default ComponentList;