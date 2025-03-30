import React, { useState } from 'react';
import axios from 'axios';
import { saveAs } from 'file-saver';
import { Document, Page, pdfjs } from 'react-pdf';
import withAuth from 'Frontend/components/withAuth';
import Lottie from 'lottie-react';
import rewriter from 'Frontend/assets/animations/resumeRewriter.json';
import { TextArea } from '@vaadin/react-components/TextArea.js';
import { Upload } from '@vaadin/react-components/Upload.js';
import { Notification } from '@vaadin/react-components/Notification.js';
import { Dialog } from '@vaadin/react-components/Dialog.js';
import { Icon } from '@vaadin/react-components/Icon.js';
import { Tab } from '@vaadin/react-components/Tab.js';
import { Tabs } from '@vaadin/react-components/Tabs.js';
import '@vaadin/icons';
import { TabSheet, TabSheetTab } from '@vaadin/react-components/TabSheet.js';
import ModifyCv from './ModifyCv';
import NewCv from './NewCv';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/4.8.69/pdf.worker.mjs`;

export const config: ViewConfig = { menu: { order: 4, icon: 'vaadin:clipboard-user' }, title: '修改简历' };

const resumeRewriter = () => {
  const [selectedTab, setSelectedTab] = useState(0);


  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      height: '100vh', // 使用全视口高度
      overflow: 'hidden' // 防止外层滚动
    }}>
      {/* 固定标签头 */}
      <div style={{
        flexShrink: 0, // 防止压缩
        backgroundColor: 'var(--lumo-base-color)',
        borderBottom: '1px solid var(--lumo-contrast-10pct)',
        padding: '0 16px' // 添加一些内边距
      }}>
        <Tabs
          selected={selectedTab}
          onSelectedChanged={(e) => setSelectedTab(e.detail.value)}
          style={{ width: '100%' }}
        >
          <Tab theme="icon-on-top">
            <Icon icon="vaadin:user" />
            <span>简历生成</span>
          </Tab>
          <Tab theme="icon-on-top">
            <Icon icon="vaadin:cog" />
            <span>完善简历</span>
          </Tab>
        </Tabs>
      </div>

      {/* 可滚动内容区域 */}
      <div style={{
        flexGrow: 1, // 占据剩余空间
        overflowY: 'auto', // 启用垂直滚动
        padding: '20px',
        backgroundColor: 'var(--lumo-contrast-5pct)'
      }}>
        <div style={{
          padding: '20px',
          backgroundColor: 'var(--lumo-base-color)',
          borderRadius: 'var(--lumo-border-radius-m)',
          boxShadow: 'var(--lumo-box-shadow-s)'
        }}>
          {selectedTab === 0 && <NewCv />}
          {selectedTab === 1 && <ModifyCv />}
        </div>
      </div>
    </div>
  );
};

export default withAuth(resumeRewriter);