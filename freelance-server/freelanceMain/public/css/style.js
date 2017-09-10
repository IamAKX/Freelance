import { StyleSheet } from 'react-native';

export default StyleSheet.create({
  'box': {
    'background': '#fff',
    'transition': 'all 0.2s ease',
    'border': [{ 'unit': 'px', 'value': 2 }, { 'unit': 'string', 'value': 'dashed' }, { 'unit': 'string', 'value': '#dadada' }],
    'marginTop': [{ 'unit': 'px', 'value': 10 }],
    'boxSizing': 'border-box',
    'borderRadius': '5px',
    'backgroundClip': 'padding-box',
    'padding': [{ 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }],
    'minHeight': [{ 'unit': 'px', 'value': 200 }]
  },
  'box:hover': {
    'border': [{ 'unit': 'px', 'value': 2 }, { 'unit': 'string', 'value': 'solid' }, { 'unit': 'string', 'value': '#525C7A' }]
  },
  'box spanbox-title': {
    'color': '#fff',
    'fontSize': [{ 'unit': 'px', 'value': 24 }],
    'fontWeight': '300',
    'textTransform': 'uppercase'
  },
  'box box-content': {
    'padding': [{ 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }, { 'unit': 'px', 'value': 10 }],
    'borderRadius': '0 0 2px 2px',
    'backgroundClip': 'padding-box',
    'boxSizing': 'border-box'
  },
  'box box-content p': {
    'color': '#515c66',
    'textTransform': 'none'
  },
  'wrapClass': {
    'wordWrap': 'break-word'
  },
  'body': {
    'overflowX': 'hidden'
  },
  // Toggle Styles
  '#wrapper': {
    'paddingLeft': [{ 'unit': 'px', 'value': 0 }],
    'WebkitTransition': 'all 0.5s ease',
    'MozTransition': 'all 0.5s ease',
    'OTransition': 'all 0.5s ease',
    'transition': 'all 0.5s ease',
    '>w768': {
      'paddingLeft': [{ 'unit': 'px', 'value': 250 }]
    }
  },
  '#wrappertoggled': {
    'paddingLeft': [{ 'unit': 'px', 'value': 250 }]
  },
  '#sidebar-wrapper': {
    'zIndex': '1000',
    'position': 'fixed',
    'left': [{ 'unit': 'px', 'value': 250 }],
    'width': [{ 'unit': 'px', 'value': 0 }],
    'height': [{ 'unit': '%V', 'value': 1 }],
    'marginLeft': [{ 'unit': 'px', 'value': -250 }],
    'overflowY': 'auto',
    'background': 'cyan',
    'WebkitTransition': 'all 0.5s ease',
    'MozTransition': 'all 0.5s ease',
    'OTransition': 'all 0.5s ease',
    'transition': 'all 0.5s ease'
  },
  '#wrappertoggled #sidebar-wrapper': {
    'width': [{ 'unit': 'px', 'value': 250 }]
  },
  '#page-content-wrapper': {
    'width': [{ 'unit': '%H', 'value': 1 }],
    'position': 'absolute',
    'padding': [{ 'unit': 'px', 'value': 15 }, { 'unit': 'px', 'value': 15 }, { 'unit': 'px', 'value': 15 }, { 'unit': 'px', 'value': 15 }]
  },
  '#wrappertoggled #page-content-wrapper': {
    'position': 'absolute',
    'marginRight': [{ 'unit': 'px', 'value': -250 }]
  },
  // Sidebar Styles
  'sidebar-nav': {
    'position': 'absolute',
    'top': [{ 'unit': 'px', 'value': 0 }],
    'width': [{ 'unit': 'px', 'value': 250 }],
    'margin': [{ 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }],
    'padding': [{ 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 0 }],
    'listStyle': 'none'
  },
  'sidebar-nav li': {
    'textIndent': '20px',
    'lineHeight': [{ 'unit': 'px', 'value': 40 }],
    'alignItems': 'center',
    'marginBottom': [{ 'unit': 'px', 'value': 20 }]
  },
  'sidebar-nav li a': {
    'display': 'block',
    'textDecoration': 'none',
    'color': '#000000',
    'fontSize': [{ 'unit': 'px', 'value': 15 }]
  },
  'sidebar-nav li a:hover': {
    'textDecoration': 'none',
    'color': '#0e3e8c',
    'background': 'rgba(255, 255, 255, 0.2)'
  },
  'sidebar-nav li a:active': {
    'textDecoration': 'none',
    'color': '#0e3e8c',
    'background': 'cyan'
  },
  'sidebar-nav li a:focus': {
    'textDecoration': 'none',
    'color': '#0e3e8c',
    'background': 'cyan'
  },
  'sidebar-nav>sidebar-brand': {
    'height': [{ 'unit': 'px', 'value': 65 }],
    'fontSize': [{ 'unit': 'px', 'value': 18 }],
    'lineHeight': [{ 'unit': 'px', 'value': 60 }],
    'fontFamily': ''Tangerine', serif'
  },
  'sidebar-nav>sidebar-brand a': {
    'color': '#032b35'
  },
  'sidebar-nav>sidebar-brand a:hover': {
    'color': '#ff0c819ef',
    'background': 'none'
  },
  'md-new': {
    'padding': [{ 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 6 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 6 }],
    'margin': [{ 'unit': 'px', 'value': 4 }, { 'unit': 'px', 'value': 6 }, { 'unit': 'px', 'value': 4 }, { 'unit': 'px', 'value': 6 }],
    'minWidth': [{ 'unit': 'px', 'value': 88 }],
    'borderRadius': '3px',
    'fontSize': [{ 'unit': 'px', 'value': 14 }],
    'textAlign': 'center',
    'textTransform': 'uppercase',
    'textDecoration': 'none',
    'border': [{ 'unit': 'string', 'value': 'none' }],
    'outline': 'none'
  },
  'listMain': {
    'fontSize': [{ 'unit': 'px', 'value': 15 }],
    'fontWeight': 'bold'
  },
  'textData': {
    'fontSize': [{ 'unit': 'px', 'value': 18 }]
  },
  'form-login': {
    'backgroundColor': '#EDEDED',
    'paddingTop': [{ 'unit': 'px', 'value': 10 }],
    'paddingBottom': [{ 'unit': 'px', 'value': 20 }],
    'paddingLeft': [{ 'unit': 'px', 'value': 20 }],
    'paddingRight': [{ 'unit': 'px', 'value': 20 }],
    'borderRadius': '15px',
    'borderColor': '#d2d2d2',
    'borderWidth': '5px',
    'boxShadow': [{ 'unit': 'px', 'value': 0 }, { 'unit': 'px', 'value': 1 }, { 'unit': 'px', 'value': 0 }, { 'unit': 'string', 'value': '#cfcfcf' }]
  },
  'form-control': {
    'borderRadius': '10px'
  },
  'wrapper': {
    'textAlign': 'center'
  }
});
