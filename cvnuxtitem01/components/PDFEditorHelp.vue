<template>
  <div class="pdf-editor-help">
    <h2>PDF编辑器使用说明</h2>
    
    <div class="help-section">
      <h3>📋 功能特点</h3>
      <ul>
        <li><strong>真正编辑:</strong> 从PDF中彻底移除敏感内容，而非仅仅覆盖</li>
        <li><strong>多种编辑方式:</strong> 手动选择、模式识别、自定义规则</li>
        <li><strong>客户端处理:</strong> 所有操作在本地完成，确保数据安全</li>
        <li><strong>批量处理:</strong> 支持多页面同时编辑</li>
        <li><strong>撤销重做:</strong> 完整的操作历史记录</li>
      </ul>
    </div>

    <div class="help-section">
      <h3>🚀 快速开始</h3>
      <ol>
        <li><strong>上传PDF文件:</strong> 拖拽或点击上传按钮选择PDF文件</li>
        <li><strong>选择编辑模式:</strong> 
          <ul>
            <li>📍 选择模式: 查看和导航PDF内容</li>
            <li>✏️ 编辑模式: 拖拽选择需要编辑的区域</li>
            <li>🔍 模式识别: 自动检测敏感信息</li>
          </ul>
        </li>
        <li><strong>添加编辑区域:</strong> 在编辑模式下拖拽选择敏感内容</li>
        <li><strong>下载结果:</strong> 点击下载按钮获取编辑后的PDF</li>
      </ol>
    </div>

    <div class="help-section">
      <h3>🎯 模式识别功能</h3>
      <p>系统内置多种敏感信息识别模式:</p>
      <div class="pattern-list">
        <div class="pattern-item">
          <span class="pattern-icon">📧</span>
          <div>
            <strong>邮箱地址</strong>
            <p>自动识别电子邮箱地址</p>
          </div>
        </div>
        <div class="pattern-item">
          <span class="pattern-icon">📱</span>
          <div>
            <strong>电话号码</strong>
            <p>识别手机号和固定电话</p>
          </div>
        </div>
        <div class="pattern-item">
          <span class="pattern-icon">🆔</span>
          <div>
            <strong>身份证号</strong>
            <p>识别中国身份证号码</p>
          </div>
        </div>
        <div class="pattern-item">
          <span class="pattern-icon">💳</span>
          <div>
            <strong>银行卡号</strong>
            <p>识别银行卡和信用卡号码</p>
          </div>
        </div>
        <div class="pattern-item">
          <span class="pattern-icon">🌐</span>
          <div>
            <strong>IP地址</strong>
            <p>识别IPv4地址</p>
          </div>
        </div>
        <div class="pattern-item">
          <span class="pattern-icon">📍</span>
          <div>
            <strong>地址信息</strong>
            <p>识别地址和位置信息</p>
          </div>
        </div>
      </div>
    </div>

    <div class="help-section">
      <h3>⌨️ 键盘快捷键</h3>
      <div class="shortcuts-grid">
        <div class="shortcut-item">
          <kbd>Ctrl + Z</kbd>
          <span>撤销</span>
        </div>
        <div class="shortcut-item">
          <kbd>Ctrl + Y</kbd>
          <span>重做</span>
        </div>
        <div class="shortcut-item">
          <kbd>Ctrl + +</kbd>
          <span>放大</span>
        </div>
        <div class="shortcut-item">
          <kbd>Ctrl + -</kbd>
          <span>缩小</span>
        </div>
        <div class="shortcut-item">
          <kbd>Ctrl + 0</kbd>
          <span>重置缩放</span>
        </div>
        <div class="shortcut-item">
          <kbd>Ctrl + S</kbd>
          <span>下载</span>
        </div>
        <div class="shortcut-item">
          <kbd>Page Up</kbd>
          <span>上一页</span>
        </div>
        <div class="shortcut-item">
          <kbd>Page Down</kbd>
          <span>下一页</span>
        </div>
      </div>
    </div>

    <div class="help-section">
      <h3>🔒 安全与隐私</h3>
      <div class="security-points">
        <div class="security-item">
          <span class="security-icon">🖥️</span>
          <div>
            <strong>本地处理</strong>
            <p>所有PDF处理都在您的浏览器中完成，文件不会上传到服务器</p>
          </div>
        </div>
        <div class="security-item">
          <span class="security-icon">🗑️</span>
          <div>
            <strong>真正删除</strong>
            <p>编辑的内容从PDF结构中完全移除，无法恢复</p>
          </div>
        </div>
        <div class="security-item">
          <span class="security-icon">🔐</span>
          <div>
            <strong>数据安全</strong>
            <p>您的敏感信息始终在您的控制之下</p>
          </div>
        </div>
      </div>
    </div>

    <div class="help-section">
      <h3>💡 使用建议</h3>
      <ul>
        <li>处理重要文档前，建议先使用示例PDF测试功能</li>
        <li>大文件处理可能需要较长时间，请耐心等待</li>
        <li>编辑前请确认选择区域准确覆盖敏感信息</li>
        <li>建议在编辑完成后验证结果PDF中的敏感信息已被移除</li>
        <li>如需处理大量相似文档，可以保存自定义识别模式</li>
      </ul>
    </div>

    <div class="help-section">
      <h3>❓ 常见问题</h3>
      <div class="faq-list">
        <details>
          <summary>编辑后的PDF文件是否安全？</summary>
          <p>是的，编辑的内容已从PDF结构中完全移除，使用专业工具也无法恢复。</p>
        </details>
        <details>
          <summary>支持哪些类型的PDF文件？</summary>
          <p>支持标准PDF文件，包括文本和图像内容。扫描版PDF需要先进行OCR处理。</p>
        </details>
        <details>
          <summary>文件大小有限制吗？</summary>
          <p>目前支持最大50MB的PDF文件，大于此大小的文件可能影响性能。</p>
        </details>
        <details>
          <summary>是否支持批量编辑？</summary>
          <p>单个PDF文件内的多页面支持批量编辑，多个文件需要逐个处理。</p>
        </details>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 纯展示组件，无需特殊逻辑
</script>

<style scoped>
.pdf-editor-help {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  line-height: 1.6;
}

.pdf-editor-help h2 {
  color: #2c3e50;
  margin-bottom: 30px;
  text-align: center;
}

.pdf-editor-help h3 {
  color: #34495e;
  margin: 30px 0 15px 0;
  border-bottom: 2px solid #3498db;
  padding-bottom: 5px;
}

.help-section {
  margin-bottom: 30px;
}

.help-section ul, .help-section ol {
  padding-left: 20px;
}

.help-section li {
  margin: 8px 0;
}

.pattern-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
  margin: 20px 0;
}

.pattern-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.pattern-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.pattern-item strong {
  color: #2c3e50;
  display: block;
  margin-bottom: 4px;
}

.pattern-item p {
  color: #6c757d;
  margin: 0;
  font-size: 14px;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin: 20px 0;
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.shortcut-item kbd {
  background: #e9ecef;
  border: 1px solid #adb5bd;
  border-radius: 3px;
  box-shadow: 0 1px 0 rgba(0,0,0,0.2);
  color: #495057;
  display: inline-block;
  font-size: 12px;
  line-height: 1.4;
  padding: 2px 6px;
  white-space: nowrap;
}

.security-points, .security-item {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.security-item {
  flex-direction: row;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #bfdbfe;
}

.security-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.security-item strong {
  color: #1e40af;
  display: block;
  margin-bottom: 4px;
}

.security-item p {
  color: #374151;
  margin: 0;
  font-size: 14px;
}

.faq-list details {
  margin: 10px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.faq-list summary {
  font-weight: 600;
  color: #2c3e50;
  cursor: pointer;
  padding: 5px 0;
}

.faq-list summary:hover {
  color: #3498db;
}

.faq-list p {
  margin: 10px 0 0 0;
  color: #555;
  padding-left: 10px;
  border-left: 3px solid #3498db;
}

@media (max-width: 768px) {
  .pdf-editor-help {
    padding: 15px;
  }
  
  .pattern-list {
    grid-template-columns: 1fr;
  }
  
  .shortcuts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .security-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>