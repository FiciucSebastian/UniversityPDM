import React from 'react';
import {IonItem, IonLabel} from '@ionic/react';
import {ComponentProps} from "./ComponentProps";

interface ComponentPropsExt extends ComponentProps {
    onEdit: (_id?: string) => void;
}

const Component: React.FC<ComponentPropsExt> = ({_id, name, quantity, releaseDate, inStock, onEdit}) => {
    // console.log("name " + name + " quantity " + quantity + "releaseDate" + releaseDate + " inStock " + inStock)
    return (
        <IonItem onClick={() => onEdit(_id)}>
            <IonLabel>{name}</IonLabel>
            <IonLabel>{quantity}</IonLabel>
            <IonLabel>{releaseDate}</IonLabel>
            <IonLabel>{inStock.toString()}</IonLabel>
        </IonItem>
    );
};

export default Component;