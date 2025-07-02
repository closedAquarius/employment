import React from 'react';
import { useSignal } from '@vaadin/hilla-react-signals';
import { Button } from '@vaadin/react-components/Button.js';
import { Dialog } from '@vaadin/react-components/Dialog.js';
import { TextField } from '@vaadin/react-components/TextField.js';
import { VerticalLayout } from '@vaadin/react-components/VerticalLayout.js';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = { title: '新增面试' };

// 假设环境变量
const apiBaseUrl = '';

const NewInterViewDialog = ({ isOpen, onClose }) => {
  const dialogOpened = useSignal(isOpen);
  return  (
    <>
      <Dialog
        headerTitle="添加面试者"
        opened={dialogOpened.value}
        onOpenedChanged={(e) => {
          dialogOpened.value = e.detail.value;
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
export default NewInterViewDialog;