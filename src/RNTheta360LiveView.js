import {UIManager, findNodeHandle, requireNativeComponent} from 'react-native';

import React from 'react';

const RNTheta360LiveView = requireNativeComponent(
  'RNTheta360LiveView',
  Theta360LiveViewModule,
  {},
);
const liveView = React.createRef();

const toggleLiveView = () => {
  UIManager.dispatchViewManagerCommand(
    findNodeHandle(liveView.current),
    'toggleLivePreview',
    [],
  );
};

export default class Theta360LiveViewModule extends React.PureComponent {
  _onConnectionChange = event => {
    if (!this.props.onConnectionChange) {
      return;
    }
    this.props.onConnectionChange(event.nativeEvent);
  };

  render() {
    return (
      <RNTheta360LiveView
        {...this.props}
        onConnectionChange={this._onConnectionChange}
        ref={liveView}
      />
    );
  }
}

export {toggleLiveView};
