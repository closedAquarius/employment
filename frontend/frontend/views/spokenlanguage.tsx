import React from 'react';
import withAuth from 'Frontend/components/withAuth';

export const config: ViewConfig = { menu: { order: 5, icon: 'vaadin:chat' }, title: '口语练习' };

const GeminiVoiceIframe = () => {
  return (
    <div style={{ width: '100%', height: '100vh' }}>
      <iframe
        src="http://localhost:7860/gemini-voice"  // 现在会通过代理访问7860端口的/gemini-voice
        style={{
          width: '100%',
          height: '100%',
          border: 'none',
          overflow: 'hidden'
        }}
        title="口语练习"
        allow="microphone"
        sandbox="allow-same-origin allow-scripts allow-forms allow-modals allow-popups allow-presentation"
      />
    </div>
  );
};

export default withAuth(GeminiVoiceIframe);