import React, { useContext, useEffect, useState } from 'react';
import {
    IonButton,
    IonButtons,
    IonContent,
    IonHeader,
    IonInput,
    IonLoading,
    IonPage,
    IonTitle,
    IonToolbar,
    IonLabel, IonCheckbox, IonDatetime, IonFabButton, IonFab
} from '@ionic/react';
import { getLogger } from '../core';
import { ComponentContext } from './ComponentProvider';
import { RouteComponentProps } from 'react-router';
import { ComponentProps } from './ComponentProps';
import moment from 'moment';

const log = getLogger('ComponentEdit');

interface ComponentEditProps extends RouteComponentProps<{
    id?: string;
}> {}

const ComponentEdit: React.FC<ComponentEditProps> = ({ history, match }) => {
    const { components, saving, savingError, saveComponent, deleteComponent } = useContext(ComponentContext);
    const [name, setName] = useState('');
    const [quantity, setQuantity] = useState(0);
    const [releaseDate, setReleaseDate] = useState('');
    const [inStock, setInStock] = useState(false);
    const [component, setComponent] = useState<ComponentProps>();
    useEffect(() => {
        log('useEffect');
        const routeId = match.params.id || '';
        const component = components?.find(it => it._id === routeId);
        setComponent(component);
        if (component) {
            setName(component.name);
            setQuantity(component.quantity);
            setReleaseDate(component.releaseDate);
            setInStock(component.inStock);
        }
    }, [match.params.id, components]);
    const handleSave = () => {
        const editedComponent = component ? { ...component, name, quantity, releaseDate, inStock } : { name, quantity, releaseDate, inStock };
        saveComponent && saveComponent(editedComponent).then(() => history.goBack());
    };

    const handleDelete = () => {
        const editedComponent = component
            ? { ...component, name, quantity, releaseDate, inStock }
            : {name, quantity, releaseDate, inStock };
        deleteComponent && deleteComponent(editedComponent).then(() => history.goBack());
    };
    log('render');
    return (
        <IonPage>
            <IonHeader>
                <IonToolbar>
                    <IonTitle>Edit</IonTitle>
                    <IonButtons slot="end">
                        <IonButton onClick={handleSave}>
                            Save
                        </IonButton>
                    </IonButtons>
                </IonToolbar>
            </IonHeader>
            <IonContent>
                <IonLabel>Component name</IonLabel>
                <IonInput value={name} onIonChange={e => setName(e.detail.value || '')} />
                <IonLabel>Quantity</IonLabel>
                <IonInput value={quantity} onIonChange={e => setQuantity(Number(e.detail.value || 0))} />
                <IonLabel>Release Date</IonLabel>
                <IonDatetime displayFormat="DD.MM.YYYY" pickerFormat="DD.MM.YYYY" value={releaseDate} onBlur={e => setReleaseDate((moment(e.target.value).format('DD.MM.YYYY')) || '')}/>
                <IonCheckbox checked={inStock} onIonChange={e => setInStock(e.detail.checked)}/>
                <IonLabel>inStock</IonLabel>
                <IonFab vertical="bottom" horizontal="start" slot="fixed">
                    <IonFabButton onClick={handleDelete}>
                        delete
                    </IonFabButton>
                </IonFab>
                <IonLoading isOpen={saving} />
                {savingError && (
                    <div>{savingError.message || 'Failed to save component'}</div>
                )}
            </IonContent>
        </IonPage>
    );
};

export default ComponentEdit;