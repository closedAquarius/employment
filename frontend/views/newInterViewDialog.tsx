import { useSignal } from '@vaadin/hilla-react-signals';
import { Button } from '@vaadin/react-components/Button.js';
import { Dialog } from '@vaadin/react-components/Dialog.js';
import { TextField } from '@vaadin/react-components/TextField.js';
import { VerticalLayout } from '@vaadin/react-components/VerticalLayout.js';
import dialogBasicStyles from './dialog-basic-styles';

const  newInterViewDialog() = ({ isOpen, onClose }) {
  return  (
    <>
      <Dialog
        headerTitle="添加面试者"
        opened={dialogOpened.value}
        onOpenedChanged={({ isOpen }) => {
          dialogOpened.value = isOpen;
        }}
        footer={
          <>
            <Button
              onClick={() => {
                dialogOpened.value = false;
              }}
            >
              Cancel
            </Button>
            <Button
              theme="primary"
              onClick={() => {
                dialogOpened.value = false;
              }}
            >
              Add
            </Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="First name" />
          <TextField label="Last name" />
        </VerticalLayout>
      </Dialog>
    </>
  );
}