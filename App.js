import {
  Button,
  StyleSheet,
  Text,
  UIManager,
  View,
  findNodeHandle,
} from 'react-native';
import RNTheta360LiveView, {toggleLiveView} from './src/RNTheta360LiveView';
import React, {useEffect, useRef} from 'react';

import RNTheta360 from './src/RNTheta360';

const App = () => {
  const checkConnection = () => {
    RNTheta360.checkThetaConnection(
      err => {
        console.log('error', err);
      },
      res => {
        console.log('res', res);
      },
    );
  };

  const connectionChanged = event => {
    console.log(event);
  };

  return (
    <View style={styles.container}>
      <Button onPress={checkConnection} title="check connction" />
      <Button onPress={toggleLiveView} title="toggle live view" />
      <RNTheta360LiveView
        //sessionId="SID_0001"
        onConnectionChange={connectionChanged}
        style={styles.liveView}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  liveViewContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'blue',
  },
  liveView: {
    width: '100%',
    height: '50%',
  },
});

export default App;
